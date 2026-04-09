package com.bike.shop.service;

import com.bike.shop.dto.response.NotificacionResponseDTO;
import com.bike.shop.entity.Bicicleta;
import com.bike.shop.entity.Notificacion;
import com.bike.shop.entity.Usuario;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.exception.ValidacionException;
import com.bike.shop.repository.NotificacionRepository;
import com.bike.shop.repository.RolRepository;
import com.bike.shop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    /**
     * Notifica a todos los usuarios con rol ADMIN.
     */
    @Transactional
    public void notificarAdmins(String titulo, String mensaje, String tipo, String urlAccion) {
        try {
            rolRepository.findByNombre("ADMIN").ifPresent(rolAdmin -> {
                List<Usuario> admins = usuarioRepository.findAll().stream()
                        .filter(u -> u.getRol().getId().equals(rolAdmin.getId())
                                && Boolean.TRUE.equals(u.getEstado()))
                        .collect(Collectors.toList());

                admins.forEach(admin -> {
                    Notificacion n = new Notificacion();
                    n.setUsuarioDestino(admin);
                    n.setTitulo(titulo);
                    n.setMensaje(mensaje);
                    n.setTipo(tipo);
                    n.setLeida(false);
                    n.setFecha(LocalDateTime.now());
                    n.setUrlAccion(urlAccion);
                    notificacionRepository.save(n);
                });
            });
        } catch (Exception e) {
            log.error("Error al notificar admins: {}", e.getMessage());
        }
    }

    /**
     * Crea notificacion de stock bajo si la cantidad <= stockMinimo.
     */
    @Transactional
    public void notificarStockBajo(Bicicleta bicicleta) {
        try {
            int minimo = bicicleta.getStockMinimo() != null ? bicicleta.getStockMinimo() : 5;
            if (bicicleta.getCantidad() <= minimo) {
                String titulo = "Stock bajo: " + bicicleta.getMarca() + " " + bicicleta.getModelo();
                String mensaje = String.format(
                        "La bicicleta %s %s (codigo %d) tiene stock %d, por debajo del minimo de %d.",
                        bicicleta.getMarca(), bicicleta.getModelo(),
                        bicicleta.getCodigo(), bicicleta.getCantidad(), minimo);
                notificarAdmins(titulo, mensaje, "warning", "/inventario");
            }
        } catch (Exception e) {
            log.error("Error al notificar stock bajo: {}", e.getMessage());
        }
    }

    public List<NotificacionResponseDTO> misNotificaciones(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return notificacionRepository
                .findByUsuarioDestinoIdOrderByFechaDesc(usuario.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificacionResponseDTO marcarLeida(Integer id, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Notificacion n = notificacionRepository
                .findByIdAndUsuarioDestinoId(id, usuario.getId())
                .orElseThrow(() -> new ValidacionException(
                        "Notificacion no encontrada o no pertenece al usuario"));

        n.setLeida(true);
        return toDTO(notificacionRepository.save(n));
    }

    public long contarNoLeidas(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return notificacionRepository.countByUsuarioDestinoIdAndLeidaFalse(usuario.getId());
    }

    private NotificacionResponseDTO toDTO(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getId(),
                n.getTitulo(),
                n.getMensaje(),
                n.getTipo(),
                n.getLeida(),
                n.getFecha(),
                n.getUrlAccion()
        );
    }
}
