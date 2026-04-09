package com.bike.shop.controller;

import com.bike.shop.dto.request.BicicletaRequestDTO;
import com.bike.shop.dto.response.BicicletaResponseDTO;
import com.bike.shop.service.BicicletaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/bicicletas")
@RequiredArgsConstructor
@CrossOrigin(origins = {
    "http://localhost:4200",
    "http://localhost:4201",
    "http://localhost:4202",
    "https://bikestore-jeduardo.netlify.app"
})
public class BicicletaController {

    private final BicicletaService bicicletaService;

    // GET públicos — catálogo del home, sin JWT
    @GetMapping
    public ResponseEntity<List<BicicletaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(bicicletaService.listarTodas());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<BicicletaResponseDTO> buscarPorCodigo(@PathVariable Integer codigo) {
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

    @GetMapping("/buscar/precio")
    public ResponseEntity<List<BicicletaResponseDTO>> buscarPorPrecio(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return ResponseEntity.ok(bicicletaService.buscarPorRangoPrecio(min, max));
    }

    // Escritura — requiere JWT y rol mínimo ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BicicletaResponseDTO> registrar(@RequestBody BicicletaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bicicletaService.registrar(dto));
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BicicletaResponseDTO> actualizar(
            @PathVariable Integer codigo, @RequestBody BicicletaRequestDTO dto) {
        return ResponseEntity.ok(bicicletaService.actualizar(codigo, dto));
    }

    @PatchMapping("/{codigo}/cantidad")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BicicletaResponseDTO> actualizarCantidad(
            @PathVariable Integer codigo, @RequestParam Integer cantidad) {
        return ResponseEntity.ok(bicicletaService.actualizarCantidad(codigo, cantidad));
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer codigo, Authentication auth) {
        bicicletaService.eliminar(codigo, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
