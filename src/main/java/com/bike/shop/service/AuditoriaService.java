package com.bike.shop.service;

import com.bike.shop.dto.response.AuditoriaResponseDTO;
import com.bike.shop.entity.Auditoria;
import com.bike.shop.entity.Usuario;
import com.bike.shop.repository.AuditoriaRepository;
import com.bike.shop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Registra un evento de auditoría. Se ejecuta en una transacción independiente
     * para que el log se guarde aunque la transacción principal falle.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String username, String accion, String modulo, String descripcion) {
        try {
            Auditoria a = new Auditoria();
            a.setAccion(accion);
            a.setModulo(modulo);
            a.setDescripcion(descripcion);
            a.setFecha(LocalDateTime.now());

            if (username != null) {
                usuarioRepository.findByUsername(username).ifPresent(a::setUsuario);
            }

            auditoriaRepository.save(a);
        } catch (Exception e) {
            log.error("Error al registrar auditoria [{}/{}]: {}", modulo, accion, e.getMessage());
        }
    }

    public List<AuditoriaResponseDTO> listar(LocalDateTime fechaInicio,
                                              LocalDateTime fechaFin,
                                              String modulo) {
        List<Auditoria> registros;

        if (fechaInicio != null && fechaFin != null && modulo != null && !modulo.isBlank()) {
            registros = auditoriaRepository.findByFechaBetweenAndModuloOrderByFechaDesc(
                    fechaInicio, fechaFin, modulo.toUpperCase());
        } else if (fechaInicio != null && fechaFin != null) {
            registros = auditoriaRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin);
        } else if (modulo != null && !modulo.isBlank()) {
            registros = auditoriaRepository.findByModuloOrderByFechaDesc(modulo.toUpperCase());
        } else {
            registros = auditoriaRepository.findAllByOrderByFechaDesc();
        }

        return registros.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private AuditoriaResponseDTO toDTO(Auditoria a) {
        Usuario u = a.getUsuario();
        return new AuditoriaResponseDTO(
                a.getId(),
                u != null ? u.getId() : null,
                u != null ? u.getUsername() : null,
                a.getAccion(),
                a.getModulo(),
                a.getDescripcion(),
                a.getFecha()
        );
    }
}
