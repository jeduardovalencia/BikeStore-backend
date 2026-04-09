package com.bike.shop.service;

import com.bike.shop.dto.response.ReporteInventarioDTO;
import com.bike.shop.dto.response.ReporteMovimientoDTO;
import com.bike.shop.dto.response.ReporteVentaDTO;
import com.bike.shop.entity.Venta;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.exception.ValidacionException;
import com.bike.shop.repository.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VentaRepository ventaRepository;
    private final BicicletaRepository bicicletaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ConfiguracionService configuracionService;

    // =========================================================
    // JSON Reports
    // =========================================================

    public List<ReporteVentaDTO> reporteVentas(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaBetween(inicio, fin)
                .stream()
                .map(v -> new ReporteVentaDTO(
                        v.getId(),
                        v.getFecha(),
                        v.getCliente().getNombre(),
                        BigDecimal.valueOf(v.getTotal()),
                        v.getEstado(),
                        v.getFormaPago()
                ))
                .collect(Collectors.toList());
    }

    public List<ReporteInventarioDTO> reporteInventario() {
        return bicicletaRepository.findAll()
                .stream()
                .map(b -> new ReporteInventarioDTO(
                        b.getCodigo(),
                        b.getMarca(),
                        b.getModelo(),
                        b.getTipo(),
                        b.getCantidad(),
                        b.getStockMinimo() != null ? b.getStockMinimo() : 5,
                        b.getPrecioVenta()
                ))
                .collect(Collectors.toList());
    }

    public List<ReporteMovimientoDTO> reporteMovimientos(LocalDateTime inicio, LocalDateTime fin) {
        List<ReporteMovimientoDTO> movimientos = new ArrayList<>();

        // SALIDAS: ventas completadas
        detalleVentaRepository.findSalidasEnPeriodo(inicio, fin)
                .forEach(dv -> movimientos.add(new ReporteMovimientoDTO(
                        dv.getVenta().getFecha(),
                        dv.getBicicleta().getMarca() + " " + dv.getBicicleta().getModelo(),
                        "SALIDA",
                        dv.getCantidad(),
                        "Venta #" + dv.getVenta().getId()
                )));

        // ENTRADAS: pedidos recibidos
        detallePedidoRepository.findEntradasEnPeriodo(inicio, fin)
                .forEach(dp -> movimientos.add(new ReporteMovimientoDTO(
                        dp.getPedido().getFecha(),
                        dp.getBicicleta().getMarca() + " " + dp.getBicicleta().getModelo(),
                        "ENTRADA",
                        dp.getCantidad(),
                        "Pedido #" + dp.getPedido().getId()
                )));

        movimientos.sort(Comparator.comparing(ReporteMovimientoDTO::getFecha).reversed());
        return movimientos;
    }

    // =========================================================
    // Excel
    // =========================================================

    public byte[] exportarVentasExcel(LocalDateTime inicio, LocalDateTime fin) {
        List<ReporteVentaDTO> data = reporteVentas(inicio, fin);

        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Ventas");

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header
            Row header = sheet.createRow(0);
            String[] cols = {"ID", "Fecha", "Cliente", "Total", "Estado", "Forma de Pago"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            // Data
            int rowNum = 1;
            for (ReporteVentaDTO v : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getFecha() != null ? v.getFecha().format(FMT) : "");
                row.createCell(2).setCellValue(v.getCliente());
                row.createCell(3).setCellValue(v.getTotal() != null ? v.getTotal().doubleValue() : 0);
                row.createCell(4).setCellValue(v.getEstado());
                row.createCell(5).setCellValue(v.getFormaPago());
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de ventas", e);
        }
    }

    public byte[] exportarInventarioExcel() {
        List<ReporteInventarioDTO> data = reporteInventario();

        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Inventario");

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row header = sheet.createRow(0);
            String[] cols = {"Codigo", "Marca", "Modelo", "Tipo", "Stock Actual", "Stock Minimo", "Precio Venta"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (ReporteInventarioDTO b : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(b.getCodigo());
                row.createCell(1).setCellValue(b.getMarca());
                row.createCell(2).setCellValue(b.getModelo());
                row.createCell(3).setCellValue(b.getTipo() != null ? b.getTipo() : "");
                row.createCell(4).setCellValue(b.getStockActual());
                row.createCell(5).setCellValue(b.getStockMinimo());
                row.createCell(6).setCellValue(b.getPrecioVenta() != null ? b.getPrecioVenta().doubleValue() : 0);
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de inventario", e);
        }
    }

    // =========================================================
    // PDF
    // =========================================================

    public byte[] exportarVentasPdf(LocalDateTime inicio, LocalDateTime fin) {
        List<ReporteVentaDTO> data = reporteVentas(inicio, fin);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            String nombreTienda = configuracionService.obtenerEntidad().getNombreTienda();
            doc.add(new Paragraph(nombreTienda).setFont(bold).setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Reporte de Ventas").setFont(bold).setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Periodo: " + inicio.format(FMT) + " - " + fin.format(FMT))
                    .setFont(normal).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph(" "));

            float[] widths = {1, 3, 3, 2, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();

            String[] headers = {"ID", "Fecha", "Cliente", "Total", "Estado", "Forma de Pago"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(bold).setFontSize(9))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            for (ReporteVentaDTO v : data) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(v.getId())).setFont(normal).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(v.getFecha() != null ? v.getFecha().format(FMT) : "").setFont(normal).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(v.getCliente()).setFont(normal).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", v.getTotal())).setFont(normal).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(v.getEstado()).setFont(normal).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(v.getFormaPago() != null ? v.getFormaPago() : "").setFont(normal).setFontSize(9)));
            }

            doc.add(table);
            doc.add(new Paragraph("\nTotal registros: " + data.size()).setFont(normal).setFontSize(10));
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de ventas", e);
        }
    }

    public byte[] exportarInventarioPdf() {
        List<ReporteInventarioDTO> data = reporteInventario();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            String nombreTienda = configuracionService.obtenerEntidad().getNombreTienda();
            doc.add(new Paragraph(nombreTienda).setFont(bold).setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Reporte de Inventario").setFont(bold).setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Generado: " + LocalDateTime.now().format(FMT))
                    .setFont(normal).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph(" "));

            float[] widths = {1, 2, 2, 1.5f, 1.5f, 1.5f, 2};
            Table table = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();

            String[] headers = {"Codigo", "Marca", "Modelo", "Tipo", "Stock Actual", "Stock Min.", "Precio Venta"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(bold).setFontSize(9))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            for (ReporteInventarioDTO b : data) {
                table.addCell(cellText(String.valueOf(b.getCodigo()), normal));
                table.addCell(cellText(b.getMarca(), normal));
                table.addCell(cellText(b.getModelo(), normal));
                table.addCell(cellText(b.getTipo() != null ? b.getTipo() : "-", normal));
                table.addCell(cellText(String.valueOf(b.getStockActual()), normal));
                table.addCell(cellText(String.valueOf(b.getStockMinimo()), normal));
                table.addCell(cellText(String.format("$%,.2f", b.getPrecioVenta()), normal));
            }

            doc.add(table);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de inventario", e);
        }
    }

    // =========================================================
    // Factura PDF por venta
    // =========================================================

    public byte[] generarFactura(Integer ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe venta con id " + ventaId));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            String nombreTienda = configuracionService.obtenerEntidad().getNombreTienda();

            // Encabezado
            doc.add(new Paragraph(nombreTienda).setFont(bold).setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("FACTURA DE VENTA").setFont(bold).setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph(String.format("N° %04d", venta.getId())).setFont(bold).setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph(" "));

            // Estado especial
            if ("pendiente_aprobacion".equals(venta.getEstado())) {
                doc.add(new Paragraph("VENTA PENDIENTE DE APROBACION - No valida como comprobante fiscal")
                        .setFont(bold).setFontSize(10)
                        .setFontColor(ColorConstants.ORANGE)
                        .setTextAlignment(TextAlignment.CENTER));
                doc.add(new Paragraph(" "));
            } else if ("rechazada".equals(venta.getEstado())) {
                doc.add(new Paragraph("VENTA RECHAZADA - Documento sin validez")
                        .setFont(bold).setFontSize(10)
                        .setFontColor(ColorConstants.RED)
                        .setTextAlignment(TextAlignment.CENTER));
                doc.add(new Paragraph(" "));
            }

            // Datos generales
            Table info = new Table(UnitValue.createPercentArray(new float[]{2, 4})).useAllAvailableWidth();
            info.addCell(cellText("Fecha:", bold));
            info.addCell(cellText(venta.getFecha() != null ? venta.getFecha().format(FMT) : "-", normal));
            info.addCell(cellText("Cliente:", bold));
            info.addCell(cellText(venta.getCliente().getNombre(), normal));
            info.addCell(cellText("Documento:", bold));
            info.addCell(cellText(venta.getCliente().getDocumento(), normal));
            info.addCell(cellText("Forma de pago:", bold));
            info.addCell(cellText(venta.getFormaPago() != null ? venta.getFormaPago() : "-", normal));
            info.addCell(cellText("Estado:", bold));
            info.addCell(cellText(venta.getEstado(), normal));
            doc.add(info);
            doc.add(new Paragraph(" "));

            // Tabla de productos
            doc.add(new Paragraph("Detalle de productos").setFont(bold).setFontSize(11));
            float[] widths = {1, 3, 1.5f, 2, 2};
            Table tabla = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();
            String[] headers = {"Cod.", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
            for (String h : headers) {
                tabla.addHeaderCell(new Cell().add(new Paragraph(h).setFont(bold).setFontSize(9))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            BigDecimal subtotalGeneral = BigDecimal.ZERO;
            var detalles = detalleVentaRepository.findByVentaId(ventaId);
            for (var d : detalles) {
                tabla.addCell(cellText(String.valueOf(d.getBicicleta().getCodigo()), normal));
                tabla.addCell(cellText(d.getBicicleta().getMarca() + " " + d.getBicicleta().getModelo(), normal));
                tabla.addCell(cellText(String.valueOf(d.getCantidad()), normal));
                tabla.addCell(cellText(String.format("$%,.2f", d.getPrecioUnitario()), normal));
                tabla.addCell(cellText(String.format("$%,.2f", d.getSubtotal()), normal));
                subtotalGeneral = subtotalGeneral.add(d.getSubtotal());
            }

            if (detalles.isEmpty()) {
                // Venta pendiente sin detalles creados aún
                tabla.addCell(new Cell(1, 5).add(
                        new Paragraph("Detalles pendientes de aprobacion").setFont(normal).setFontSize(9)
                                .setTextAlignment(TextAlignment.CENTER)));
            }

            doc.add(tabla);
            doc.add(new Paragraph(" "));

            // Total
            Table totales = new Table(UnitValue.createPercentArray(new float[]{5, 2})).useAllAvailableWidth();
            totales.addCell(new Cell().add(new Paragraph("TOTAL").setFont(bold).setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT)).setBorder(null));
            totales.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", venta.getTotal()))
                    .setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.RIGHT)).setBorder(null));
            doc.add(totales);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Gracias por su compra.").setFont(normal).setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando factura PDF", e);
        }
    }

    private Cell cellText(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(9));
    }
}
