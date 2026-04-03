// ProveedorService
package com.bike.shop.service;

import com.bike.shop.dto.request.ProveedorRequestDTO;
import com.bike.shop.dto.response.ProveedorResponseDTO;
import com.bike.shop.entity.Proveedor;
import com.bike.shop.exception.DuplicateResourceException;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public List<ProveedorResponseDTO> listarTodos() {
        return proveedorRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProveedorResponseDTO buscarPorId(Integer id) {
        return toResponseDTO(proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe proveedor con id " + id)));
    }

    public ProveedorResponseDTO crear(ProveedorRequestDTO dto) {
        Proveedor p = new Proveedor();
        p.setNombre(dto.getNombre());
        p.setTelefono(dto.getTelefono());
        p.setEmail(dto.getEmail());
        p.setFrecuenciaEntrega(dto.getFrecuenciaEntrega());
        return toResponseDTO(proveedorRepository.save(p));
    }

    public ProveedorResponseDTO actualizar(Integer id, ProveedorRequestDTO dto) {
        Proveedor p = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe proveedor con id " + id));
        p.setNombre(dto.getNombre());
        p.setTelefono(dto.getTelefono());
        p.setEmail(dto.getEmail());
        p.setFrecuenciaEntrega(dto.getFrecuenciaEntrega());
        return toResponseDTO(proveedorRepository.save(p));
    }

    public void eliminar(Integer id) {
        if (!proveedorRepository.existsById(id))
            throw new RecursoNoEncontradoException("No existe proveedor con id " + id);
        proveedorRepository.deleteById(id);
    }

    private ProveedorResponseDTO toResponseDTO(Proveedor p) {
        return new ProveedorResponseDTO(
                p.getId(),
                p.getNombre(),
                p.getTelefono(),
                p.getEmail(),
                p.getFrecuenciaEntrega()
        );
    }
}