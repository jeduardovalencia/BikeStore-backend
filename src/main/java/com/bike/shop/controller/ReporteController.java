package com.bike.shop.controller;

import com.bike.shop.dto.response.ReporteInventarioDTO;
import com.bike.shop.dto.response.ReporteMovimientoDTO;
import com.bike.shop.dto.response.ReporteVentaDTO;
import com.bike.shop.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = {
    "http://localhost:4200",
    "http://localhost:4201",
    "https://bikestore-jeduardo.netlify.app"
})
public class ReporteController {

    private final ReporteService reporteService;
    private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    // ── JSON ──────────────────────────────────────────────

    @GetMapping("/ventas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReporteVentaDTO>> reporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(reporteService.reporteVentas(fechaInicio, fechaFin));
    }

    @GetMapping("/inventario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReporteInventarioDTO>> reporteInventario() {
        return ResponseEntity.ok(reporteService.reporteInventario());
    }

    @GetMapping("/entradas-salidas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReporteMovimientoDTO>> reporteMovimientos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(reporteService.reporteMovimientos(fechaInicio, fechaFin));
    }

    // ── Excel ─────────────────────────────────────────────

    @GetMapping("/ventas/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> ventasExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        byte[] data = reporteService.exportarVentasExcel(fechaInicio, fechaFin);
        String filename = "ventas_" + LocalDateTime.now().format(FILE_FMT) + ".xlsx";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(data);
    }

    @GetMapping("/inventario/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> inventarioExcel() {
        byte[] data = reporteService.exportarInventarioExcel();
        String filename = "inventario_" + LocalDateTime.now().format(FILE_FMT) + ".xlsx";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(data);
    }

    // ── PDF ───────────────────────────────────────────────

    @GetMapping("/ventas/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> ventasPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        byte[] data = reporteService.exportarVentasPdf(fechaInicio, fechaFin);
        String filename = "ventas_" + LocalDateTime.now().format(FILE_FMT) + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(data);
    }

    @GetMapping("/inventario/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> inventarioPdf() {
        byte[] data = reporteService.exportarInventarioPdf();
        String filename = "inventario_" + LocalDateTime.now().format(FILE_FMT) + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(data);
    }
}
