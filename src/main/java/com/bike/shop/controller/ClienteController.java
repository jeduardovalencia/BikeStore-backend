package com.bike.shop.controller;

import com.bike.shop.dto.ClienteDTO;
import com.bike.shop.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // GET /clientes
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    // GET /clientes/{documento}
    @GetMapping("/{documento}")
    public ResponseEntity<ClienteDTO> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(clienteService.buscarPorDocumento(documento));
    }

    // GET /clientes/buscar?termino=xxx
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDTO>> buscar(@RequestParam String termino) {
        return ResponseEntity.ok(clienteService.buscarPorTermino(termino));
    }

    // POST /clientes
    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody ClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(dto));
    }

    // PUT /clientes/{documento}
    @PutMapping("/{documento}")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable String documento,
                                                 @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizar(documento, dto));
    }
}
