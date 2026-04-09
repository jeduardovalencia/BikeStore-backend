package com.bike.shop.controller;

import com.bike.shop.dto.request.PedidoRequestDTO;
import com.bike.shop.dto.response.PedidoResponseDTO;
import com.bike.shop.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<PedidoResponseDTO> crear(@RequestBody PedidoRequestDTO dto,
                                                    Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crear(dto, auth.getName()));
    }

    @PatchMapping("/{id}/recibido")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<PedidoResponseDTO> marcarRecibido(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.marcarRecibido(id));
    }

    @GetMapping("/proveedor/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorProveedor(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.buscarPorProveedor(id));
    }
}
