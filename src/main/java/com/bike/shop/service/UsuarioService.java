package com.bike.shop.service;

import com.bike.shop.dto.request.UsuarioRequestDTO;
import com.bike.shop.dto.response.UsuarioResponseDTO;
import com.bike.shop.entity.Rol;
import com.bike.shop.entity.Usuario;
import com.bike.shop.exception.DuplicateResourceException;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.repository.RolRepository;
import com.bike.shop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Integer id) {
        return toResponseDTO(usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe usuario con id " + id)));
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername()))
            throw new DuplicateResourceException(
                    "Ya existe un usuario con username: " + dto.getUsername());
        if (dto.getEmail() != null && usuarioRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException(
                    "Ya existe un usuario con email: " + dto.getEmail());

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe rol con id " + dto.getRolId()));

        Usuario u = new Usuario();
        u.setUsername(dto.getUsername());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setNombre(dto.getNombre());
        u.setEmail(dto.getEmail());
        u.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        u.setRol(rol);

        return toResponseDTO(usuarioRepository.save(u));
    }

    public UsuarioResponseDTO actualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe usuario con id " + id));

        if (usuarioRepository.existsByUsernameAndIdNot(dto.getUsername(), id))
            throw new DuplicateResourceException(
                    "Ya existe un usuario con username: " + dto.getUsername());
        if (dto.getEmail() != null && usuarioRepository.existsByEmailAndIdNot(dto.getEmail(), id))
            throw new DuplicateResourceException(
                    "Ya existe un usuario con email: " + dto.getEmail());

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe rol con id " + dto.getRolId()));

        u.setUsername(dto.getUsername());
        u.setNombre(dto.getNombre());
        u.setEmail(dto.getEmail());
        u.setEstado(dto.getEstado());
        u.setRol(rol);

        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            u.setPassword(passwordEncoder.encode(dto.getPassword()));

        return toResponseDTO(usuarioRepository.save(u));
    }

    public void eliminar(Integer id, String username) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe usuario con id " + id));
        usuarioRepository.deleteById(id);
        auditoriaService.registrar(username, "ELIMINAR", "USUARIOS",
                "Usuario eliminado: " + u.getUsername() + " (id " + id + ")");
    }

    private UsuarioResponseDTO toResponseDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getNombre(),
                u.getEmail(),
                u.getEstado(),
                u.getRol().getId(),
                u.getRol().getNombre()
        );
    }
}
