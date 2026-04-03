package com.bike.shop.service;

import com.bike.shop.dto.ClienteDTO;
import com.bike.shop.entity.Cliente;
import com.bike.shop.exception.DuplicateResourceException;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // GET todos los clientes
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET buscar por documento
    public ClienteDTO buscarPorDocumento(String documento) {
        Cliente cliente = clienteRepository.findById(documento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + documento));
        return toDTO(cliente);
    }

    // GET buscar por término (nombre, documento, email)
    public List<ClienteDTO> buscarPorTermino(String termino) {
        return clienteRepository.buscarPorTermino(termino)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // POST crear cliente
    public ClienteDTO crear(ClienteDTO dto) {
        if (clienteRepository.existsById(dto.getDocumento())) {
            throw new DuplicateResourceException("Ya existe un cliente con documento: " + dto.getDocumento());
        }
        Cliente cliente = toEntity(dto);
        return toDTO(clienteRepository.save(cliente));
    }

    // PUT actualizar cliente
    public ClienteDTO actualizar(String documento, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(documento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado: " + documento));
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setDireccion(dto.getDireccion());
        return toDTO(clienteRepository.save(cliente));
    }

    // Convertir Entity → DTO
    private ClienteDTO toDTO(Cliente c) {
        return new ClienteDTO(
                c.getDocumento(),
                c.getNombre(),
                c.getTelefono(),
                c.getEmail(),
                c.getDireccion()
        );
    }

    // Convertir DTO → Entity
    private Cliente toEntity(ClienteDTO dto) {
        return new Cliente(
                dto.getDocumento(),
                dto.getNombre(),
                dto.getTelefono(),
                dto.getEmail(),
                dto.getDireccion()
        );
    }
}