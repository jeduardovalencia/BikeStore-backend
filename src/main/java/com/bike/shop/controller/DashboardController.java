package com.bike.shop.controller;

import com.bike.shop.repository.BicicletaRepository;
import com.bike.shop.repository.ClienteRepository;
import com.bike.shop.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final BicicletaRepository bicicletaRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalVentas", ventaRepository.count());
        data.put("totalClientes", clienteRepository.count());
        data.put("totalBicicletas", bicicletaRepository.count());
        data.put("stockBajo", bicicletaRepository.findStockBajo().size());
        data.put("sinStock", bicicletaRepository.findSinStock().size());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/top-bicicletas")
    public ResponseEntity<List<Map<String, Object>>> getTopBicicletas() {
        List<Object[]> resultados = ventaRepository.findTopBicicletas();
        List<Map<String, Object>> top = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> item = new HashMap<>();
            item.put("codigo", fila[0]);
            item.put("marca", fila[1]);
            item.put("modelo", fila[2]);
            item.put("totalVendido", fila[3]);
            top.add(item);
        }
        return ResponseEntity.ok(top);
    }
}


