// VentaController
package com.bike.shop.controller;


import com.bike.shop.dto.request.VentaRequestDTO;
import com.bike.shop.dto.response.VentaResponseDTO;
import com.bike.shop.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{documento}")
    public ResponseEntity<List<VentaResponseDTO>> buscarPorCliente(
            @PathVariable String documento) {
        return ResponseEntity.ok(ventaService.buscarPorCliente(documento));
    }

    @PostMapping
    public ResponseEntity<VentaResponseDTO> crear(@RequestBody VentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.crear(dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<VentaResponseDTO> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.cancelar(id));
    }

    @GetMapping("/reporte")
    public ResponseEntity<List<VentaResponseDTO>> reporte(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {
        return ResponseEntity.ok(ventaService.buscarPorFecha(inicio, fin));
    }
}