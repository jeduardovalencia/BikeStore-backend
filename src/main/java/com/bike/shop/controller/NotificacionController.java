package com.bike.shop.controller;

import com.bike.shop.dto.response.NotificacionResponseDTO;
import com.bike.shop.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = {
    "http://localhost:4200",
    "http://localhost:4201",
    "https://bikestore-jeduardo.netlify.app"
})
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping("/mis-notificaciones")
    public ResponseEntity<List<NotificacionResponseDTO>> misNotificaciones(Authentication auth) {
        return ResponseEntity.ok(notificacionService.misNotificaciones(auth.getName()));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarLeida(
            @PathVariable Integer id, Authentication auth) {
        return ResponseEntity.ok(notificacionService.marcarLeida(id, auth.getName()));
    }

    @GetMapping("/no-leidas/count")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(Authentication auth) {
        long count = notificacionService.contarNoLeidas(auth.getName());
        return ResponseEntity.ok(Map.of("count", count));
    }
}
