// VentaService
package com.bike.shop.service;

import com.bike.shop.dto.response.DetalleVentaResponseDTO;
import com.bike.shop.dto.request.VentaRequestDTO;
import com.bike.shop.dto.response.VentaResponseDTO;
import com.bike.shop.entity.Cliente;
import com.bike.shop.entity.DetalleVenta;
import com.bike.shop.entity.Venta;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.exception.ValidacionException;
import com.bike.shop.repository.BicicletaRepository;
import com.bike.shop.repository.ClienteRepository;
import com.bike.shop.repository.DetalleVentaRepository;
import com.bike.shop.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final BicicletaRepository bicicletaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    // GET todas las ventas
    public List<VentaResponseDTO> listarTodas() {
        return ventaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // GET venta por id
    public VentaResponseDTO buscarPorId(Integer id) {
        return toResponseDTO(ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe venta con id " + id)));
    }

    // GET ventas por cliente
    public List<VentaResponseDTO> buscarPorCliente(String documento) {
        return ventaRepository.findByClienteDocumento(documento)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // POST crear venta
    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO dto) {
        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getDocumentoCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe cliente con documento " + dto.getDocumentoCliente()));

        // Validar detalles
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty())
            throw new ValidacionException("La venta debe tener al menos un detalle");

        // Crear venta
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now());
        venta.setFormaPago(dto.getFormaPago());
        venta.setEstado("completada");
        venta.setTotal(0.0);
        Venta guardada = ventaRepository.save(venta);

        // Crear detalles — los triggers manejan stock y total
        dto.getDetalles().forEach(d -> {
            bicicletaRepository.findById(d.getCodigoBicicleta())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe bicicleta con código " + d.getCodigoBicicleta()));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(guardada);
            detalle.setBicicleta(bicicletaRepository.findById(
                    d.getCodigoBicicleta()).get());
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(java.math.BigDecimal.ZERO);
            detalle.setSubtotal(java.math.BigDecimal.ZERO);
            detalleVentaRepository.save(detalle);
        });

        // Recargar venta con total actualizado por trigger
        return toResponseDTO(ventaRepository.findById(guardada.getId()).get());
    }

    // PATCH cancelar venta
    @Transactional
    public VentaResponseDTO cancelar(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe venta con id " + id));

        if (venta.getEstado().equals("devuelta"))
            throw new ValidacionException("La venta ya fue cancelada");

        venta.setEstado("devuelta");
        return toResponseDTO(ventaRepository.save(venta));
    }

    private VentaResponseDTO toResponseDTO(Venta v) {
        List<DetalleVentaResponseDTO> detalles = detalleVentaRepository
                .findByVentaId(v.getId())
                .stream()
                .map(d -> new DetalleVentaResponseDTO(
                        d.getId(),
                        d.getBicicleta().getCodigo(),
                        d.getBicicleta().getMarca() + " " + d.getBicicleta().getModelo(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new VentaResponseDTO(
                v.getId(),
                v.getCliente().getDocumento(),
                v.getCliente().getNombre(),
                v.getFecha(),
                java.math.BigDecimal.valueOf(v.getTotal()),
                v.getFormaPago(),
                v.getEstado(),
                detalles
        );
    }

    public List<VentaResponseDTO> buscarPorFecha(
            LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaBetween(inicio, fin)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}