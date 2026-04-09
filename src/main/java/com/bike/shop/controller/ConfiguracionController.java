package com.bike.shop.controller;

import com.bike.shop.dto.request.ConfiguracionRequestDTO;
import com.bike.shop.dto.response.ConfiguracionResponseDTO;
import com.bike.shop.service.ConfiguracionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
@CrossOrigin(origins = {
    "http://localhost:4200",
    "http://localhost:4201",
    "https://bikestore-jeduardo.netlify.app"
})
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfiguracionResponseDTO> obtener() {
        return ResponseEntity.ok(configuracionService.obtener());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfiguracionResponseDTO> actualizar(
            @Valid @RequestBody ConfiguracionRequestDTO dto) {
        return ResponseEntity.ok(configuracionService.actualizar(dto));
    }
}
