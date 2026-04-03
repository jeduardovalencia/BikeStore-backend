// PedidoService
package com.bike.shop.service;

import com.bike.shop.dto.response.DetallePedidoResponseDTO;
import com.bike.shop.dto.request.PedidoRequestDTO;
import com.bike.shop.dto.response.PedidoResponseDTO;
import com.bike.shop.entity.Bicicleta;
import com.bike.shop.entity.DetallePedido;
import com.bike.shop.entity.Pedido;
import com.bike.shop.entity.Proveedor;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.exception.ValidacionException;
import com.bike.shop.repository.BicicletaRepository;
import com.bike.shop.repository.DetallePedidoRepository;
import com.bike.shop.repository.PedidoRepository;
import com.bike.shop.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProveedorRepository proveedorRepository;
    private final BicicletaRepository bicicletaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarPorId(Integer id) {
        return toResponseDTO(pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe pedido con id " + id)));
    }

    @Transactional
    public PedidoResponseDTO crear(PedidoRequestDTO dto) {
        // Validar proveedor
        Proveedor proveedor = proveedorRepository.findById(dto.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe proveedor con id " + dto.getIdProveedor()));

        // Validar detalles
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty())
            throw new ValidacionException("El pedido debe tener al menos un detalle");

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setProveedor(proveedor);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("pendiente");
        Pedido guardado = pedidoRepository.save(pedido);

        // Crear detalles
        dto.getDetalles().forEach(d -> {
            Bicicleta bicicleta = bicicletaRepository.findById(d.getCodigoBicicleta())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe bicicleta con código " + d.getCodigoBicicleta()));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(guardado);
            detalle.setBicicleta(bicicleta);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioCostoUnitario(d.getPrecioCostoUnitario());
            detallePedidoRepository.save(detalle);
        });

        return toResponseDTO(guardado);
    }

    @Transactional
    public PedidoResponseDTO marcarRecibido(Integer id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe pedido con id " + id));

        if (pedido.getEstado().equals("recibido"))
            throw new ValidacionException("El pedido ya fue recibido");

        pedido.setEstado("recibido");
        return toResponseDTO(pedidoRepository.save(pedido));
    }

    private PedidoResponseDTO toResponseDTO(Pedido p) {
        List<DetallePedidoResponseDTO> detalles = detallePedidoRepository
                .findByPedidoId(p.getId())
                .stream()
                .map(d -> new DetallePedidoResponseDTO(
                        d.getId(),
                        d.getBicicleta().getCodigo(),
                        d.getBicicleta().getMarca() + " " + d.getBicicleta().getModelo(),
                        d.getCantidad(),
                        d.getPrecioCostoUnitario()
                ))
                .collect(Collectors.toList());

        return new PedidoResponseDTO(
                p.getId(),
                p.getProveedor().getId(),
                p.getProveedor().getNombre(),
                p.getFecha(),
                p.getEstado(),
                detalles
        );
    }

    public List<PedidoResponseDTO> buscarPorProveedor(Integer idProveedor) {
        return pedidoRepository.findByProveedorId(idProveedor)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}