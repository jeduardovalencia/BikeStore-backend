package com.bike.shop.controller;

import com.bike.shop.dto.request.RechazarVentaRequestDTO;
import com.bike.shop.dto.request.VentaRequestDTO;
import com.bike.shop.dto.response.VentaResponseDTO;
import com.bike.shop.service.ReporteService;
import com.bike.shop.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {

    private final VentaService ventaService;
    private final ReporteService reporteService;
    private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<VentaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<VentaResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{documento}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<VentaResponseDTO>> buscarPorCliente(
            @PathVariable String documento) {
        return ResponseEntity.ok(ventaService.buscarPorCliente(documento));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<VentaResponseDTO> crear(@RequestBody VentaRequestDTO dto,
                                                   Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.crear(dto, auth.getName()));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<VentaResponseDTO> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.cancelar(id));
    }

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VentaResponseDTO> aprobar(@PathVariable Integer id,
                                                     Authentication auth) {
        return ResponseEntity.ok(ventaService.aprobar(id, auth.getName()));
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VentaResponseDTO> rechazar(@PathVariable Integer id,
                                                      @Valid @RequestBody RechazarVentaRequestDTO dto,
                                                      Authentication auth) {
        return ResponseEntity.ok(ventaService.rechazar(id, dto.getObservacion(), auth.getName()));
    }

    @GetMapping("/{id}/factura")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<byte[]> factura(@PathVariable Integer id) {
        byte[] pdf = reporteService.generarFactura(id);
        String filename = "factura_" + String.format("%04d", id) + "_" +
                LocalDateTime.now().format(FILE_FMT) + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(pdf);
    }

    @GetMapping("/pendientes-aprobacion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VentaResponseDTO>> listarPendientes() {
        return ResponseEntity.ok(ventaService.listarPendientes());
    }

    @GetMapping("/pendientes-aprobacion/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.Map<String, Long>> contarPendientes() {
        return ResponseEntity.ok(java.util.Map.of("count", ventaService.contarPendientes()));
    }

    @GetMapping("/reporte")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<VentaResponseDTO>> reporte(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(ventaService.buscarPorFecha(inicio, fin));
    }
}
