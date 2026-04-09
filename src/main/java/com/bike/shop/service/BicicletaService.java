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
    private final AuditoriaService auditoriaService;

    public List<BicicletaResponseDTO> listarTodas() {
        return bicicletaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BicicletaResponseDTO buscarPorCodigo(Integer codigo) {
        return toResponseDTO(bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe bicicleta con codigo " + codigo)));
    }

    public List<BicicletaResponseDTO> buscarPorMarca(String marca) {
        return bicicletaRepository.findByMarcaIgnoreCase(marca)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BicicletaResponseDTO> buscarPorTipo(String tipo) {
        return bicicletaRepository.findByTipoIgnoreCase(tipo)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BicicletaResponseDTO> listarStockBajo() {
        return bicicletaRepository.findStockBajo()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BicicletaResponseDTO registrar(BicicletaRequestDTO dto) {
        validar(dto);
        return toResponseDTO(bicicletaRepository.save(toEntity(dto)));
    }

    @Transactional
    public BicicletaResponseDTO actualizar(Integer codigo, BicicletaRequestDTO dto) {
        Bicicleta b = bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe bicicleta con codigo " + codigo));
        validar(dto);
        b.setMarca(dto.getMarca());
        b.setModelo(dto.getModelo());
        b.setTipo(dto.getTipo());
        b.setPrecioCosto(dto.getPrecioCosto());
        b.setPrecioVenta(dto.getPrecioVenta());
        b.setDescripcion(dto.getDescripcion());
        b.setImagen(dto.getImagen());
        b.setCantidad(dto.getCantidad() != null ? dto.getCantidad() : b.getCantidad());
        b.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : b.getStockMinimo());
        b.setStockMaximo(dto.getStockMaximo() != null ? dto.getStockMaximo() : b.getStockMaximo());
        return toResponseDTO(bicicletaRepository.save(b));
    }

    @Transactional
    public void eliminar(Integer codigo, String username) {
        Bicicleta b = bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe bicicleta con codigo " + codigo));
        bicicletaRepository.deleteById(codigo);
        auditoriaService.registrar(username, "ELIMINAR", "BICICLETAS",
                "Bicicleta eliminada: " + b.getMarca() + " " + b.getModelo() + " (codigo " + codigo + ")");
    }

    @Transactional
    public BicicletaResponseDTO actualizarCantidad(Integer codigo, Integer cantidad) {
        if (cantidad < 0)
            throw new ValidacionException("La cantidad no puede ser negativa");
        Bicicleta b = bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe bicicleta con codigo " + codigo));
        b.setCantidad(cantidad);
        return toResponseDTO(bicicletaRepository.save(b));
    }

    public List<BicicletaResponseDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        return bicicletaRepository.findByRangoPrecio(min, max)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void validar(BicicletaRequestDTO dto) {
        if (dto.getMarca() == null || dto.getMarca().isBlank())
            throw new ValidacionException("La marca es obligatoria");
        if (dto.getModelo() == null || dto.getModelo().isBlank())
            throw new ValidacionException("El modelo es obligatorio");
        if (dto.getPrecioVenta() == null || dto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidacionException("El precio de venta debe ser mayor a 0");
        if (dto.getPrecioCosto() == null || dto.getPrecioCosto().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidacionException("El precio de costo debe ser mayor a 0");
    }

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
                b.getDescripcion(),
                b.getImagen()
        );
    }

    private Bicicleta toEntity(BicicletaRequestDTO dto) {
        Bicicleta b = new Bicicleta();
        b.setMarca(dto.getMarca());
        b.setModelo(dto.getModelo());
        b.setTipo(dto.getTipo());
        b.setPrecioCosto(dto.getPrecioCosto());
        b.setPrecioVenta(dto.getPrecioVenta());
        b.setDescripcion(dto.getDescripcion());
        b.setImagen(dto.getImagen());
        b.setCantidad(dto.getCantidad() != null ? dto.getCantidad() : 0);
        b.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 5);
        b.setStockMaximo(dto.getStockMaximo() != null ? dto.getStockMaximo() : 50);
        return b;
    }
}
