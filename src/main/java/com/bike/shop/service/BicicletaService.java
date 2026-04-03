package com.bike.shop.service;

import com.bike.shop.dto.request.BicicletaRequestDTO;
import com.bike.shop.dto.response.BicicletaResponseDTO;
import com.bike.shop.entity.Bicicleta;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.exception.ValidacionException;
import com.bike.shop.repository.BicicletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BicicletaService {

    private final BicicletaRepository bicicletaRepository;

    // GET todas
    public List<BicicletaResponseDTO> listarTodas() {
        return bicicletaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // GET por código
    public BicicletaResponseDTO buscarPorCodigo(Integer codigo) {
        return toResponseDTO(bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe bicicleta con código " + codigo)));
    }

    // GET por marca
    public List<BicicletaResponseDTO> buscarPorMarca(String marca) {
        return bicicletaRepository.findByMarcaIgnoreCase(marca)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // GET por tipo
    public List<BicicletaResponseDTO> buscarPorTipo(String tipo) {
        return bicicletaRepository.findByTipoIgnoreCase(tipo)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // GET stock bajo
    public List<BicicletaResponseDTO> listarStockBajo() {
        return bicicletaRepository.findStockBajo()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // POST registrar
    @Transactional
    public BicicletaResponseDTO registrar(BicicletaRequestDTO dto) {
        validar(dto);
        return toResponseDTO(bicicletaRepository.save(toEntity(dto)));
    }

    // PUT actualizar
    @Transactional
    public BicicletaResponseDTO actualizar(Integer codigo, BicicletaRequestDTO dto) {
        Bicicleta b = bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe bicicleta con código " + codigo));
        validar(dto);
        b.setMarca(dto.getMarca());
        b.setModelo(dto.getModelo());
        b.setTipo(dto.getTipo());
        b.setPrecioCosto(dto.getPrecioCosto());
        b.setPrecioVenta(dto.getPrecioVenta());
        b.setDescripcion(dto.getDescripcion());
        return toResponseDTO(bicicletaRepository.save(b));
    }

    // DELETE
    @Transactional
    public void eliminar(Integer codigo) {
        if (!bicicletaRepository.existsById(codigo)) {
            throw new RecursoNoEncontradoException(
                    "No existe bicicleta con código " + codigo);
        }
        bicicletaRepository.deleteById(codigo);
    }

    // PATCH actualizar cantidad
    @Transactional
    public BicicletaResponseDTO actualizarCantidad(Integer codigo, Integer cantidad) {
        if (cantidad < 0) {
            throw new ValidacionException("La cantidad no puede ser negativa");
        }
        Bicicleta b = bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe bicicleta con código " + codigo));
        b.setCantidad(cantidad);
        return toResponseDTO(bicicletaRepository.save(b));
    }

    public List<BicicletaResponseDTO> buscarPorRangoPrecio(
            BigDecimal min, BigDecimal max) {
        return bicicletaRepository.findByRangoPrecio(min, max)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Validaciones
    private void validar(BicicletaRequestDTO dto) {
        if (dto.getMarca() == null || dto.getMarca().isBlank())
            throw new ValidacionException("La marca es obligatoria");
        if (dto.getModelo() == null || dto.getModelo().isBlank())
            throw new ValidacionException("El modelo es obligatorio");
        if (dto.getPrecioVenta() == null ||
                dto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidacionException("El precio de venta debe ser mayor a 0");
        if (dto.getPrecioCosto() == null ||
                dto.getPrecioCosto().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidacionException("El precio de costo debe ser mayor a 0");
    }

    // Entity → ResponseDTO
    private BicicletaResponseDTO toResponseDTO(Bicicleta b) {
        return new BicicletaResponseDTO(
                b.getCodigo(),
                b.getMarca(),
                b.getModelo(),
                b.getTipo(),
                b.getPrecioCosto(),
                b.getPrecioVenta(),
                b.getCantidad(),
                b.getStockMinimo(),
                b.getStockMaximo(),
                b.getDescripcion()
        );
    }

    // RequestDTO → Entity
    private Bicicleta toEntity(BicicletaRequestDTO dto) {
        Bicicleta b = new Bicicleta();
        b.setMarca(dto.getMarca());
        b.setModelo(dto.getModelo());
        b.setTipo(dto.getTipo());
        b.setPrecioCosto(dto.getPrecioCosto());
        b.setPrecioVenta(dto.getPrecioVenta());
        b.setDescripcion(dto.getDescripcion());
        b.setCantidad(0);
        b.setStockMinimo(5);
        b.setStockMaximo(50);
        return b;
    }


}