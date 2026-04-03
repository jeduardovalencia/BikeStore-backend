package com.bike.shop.controller;

import com.bike.shop.dto.request.BicicletaRequestDTO;
import com.bike.shop.dto.response.BicicletaResponseDTO;
import com.bike.shop.service.BicicletaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

import java.util.List;

@RestController
@RequestMapping("/api/bicicletas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BicicletaController {

    private final BicicletaService bicicletaService;

    @GetMapping
    public ResponseEntity<List<BicicletaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(bicicletaService.listarTodas());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<BicicletaResponseDTO> buscarPorCodigo(
            @PathVariable Integer codigo) {
        return ResponseEntity.ok(bicicletaService.buscarPorCodigo(codigo));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<BicicletaResponseDTO>> buscar(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String tipo) {
        if (marca != null && !marca.isBlank())
            return ResponseEntity.ok(bicicletaService.buscarPorMarca(marca));
        if (tipo != null && !tipo.isBlank())
            return ResponseEntity.ok(bicicletaService.buscarPorTipo(tipo));
        return ResponseEntity.ok(bicicletaService.listarTodas());
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<BicicletaResponseDTO>> stockBajo() {
        return ResponseEntity.ok(bicicletaService.listarStockBajo());
    }

    @PostMapping
    public ResponseEntity<BicicletaResponseDTO> registrar(
            @RequestBody BicicletaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bicicletaService.registrar(dto));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<BicicletaResponseDTO> actualizar(
            @PathVariable Integer codigo,
            @RequestBody BicicletaRequestDTO dto) {
        return ResponseEntity.ok(bicicletaService.actualizar(codigo, dto));
    }

    @PatchMapping("/{codigo}/cantidad")
    public ResponseEntity<BicicletaResponseDTO> actualizarCantidad(
            @PathVariable Integer codigo,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(bicicletaService.actualizarCantidad(codigo, cantidad));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer codigo) {
        bicicletaService.eliminar(codigo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar/precio")
    public ResponseEntity<List<BicicletaResponseDTO>> buscarPorPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(
                bicicletaService.buscarPorRangoPrecio(min, max));
    }
}