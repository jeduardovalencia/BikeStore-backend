package com.bike.shop.service;

import com.bike.shop.dto.response.DetalleVentaResponseDTO;
import com.bike.shop.dto.request.DetalleVentaRequestDTO;
import com.bike.shop.dto.request.VentaRequestDTO;
import com.bike.shop.dto.response.VentaResponseDTO;
import com.bike.shop.entity.*;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.exception.ValidacionException;
import com.bike.shop.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final BicicletaRepository bicicletaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AprobacionVentaRepository aprobacionVentaRepository;
    private final ConfiguracionService configuracionService;
    private final NotificacionService notificacionService;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    public List<VentaResponseDTO> listarTodas() {
        return ventaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public VentaResponseDTO buscarPorId(Integer id) {
        return toResponseDTO(ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe venta con id " + id)));
    }

    public List<VentaResponseDTO> buscarPorCliente(String documento) {
        return ventaRepository.findByClienteDocumento(documento)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO dto, String username) {
        Cliente cliente = clienteRepository.findById(dto.getDocumentoCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe cliente con documento " + dto.getDocumentoCliente()));

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty())
            throw new ValidacionException("La venta debe tener al menos un detalle");

        // Calcular total manualmente y validar bicicletas
        BigDecimal totalCalculado = BigDecimal.ZERO;
        for (DetalleVentaRequestDTO d : dto.getDetalles()) {
            Bicicleta b = bicicletaRepository.findById(d.getCodigoBicicleta())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe bicicleta con codigo " + d.getCodigoBicicleta()));
            totalCalculado = totalCalculado.add(
                    b.getPrecioVenta().multiply(BigDecimal.valueOf(d.getCantidad())));
        }

        Configuracion config = configuracionService.obtenerEntidad();

        if (totalCalculado.compareTo(config.getUmbralAprobacionVenta()) > 0) {
            return crearVentaPendiente(dto, cliente, totalCalculado, username, config);
        } else {
            return crearVentaNormal(dto, cliente, username);
        }
    }

    /**
     * Crea venta pendiente de aprobacion. NO crea detalle_venta para no disparar triggers de stock.
     */
    private VentaResponseDTO crearVentaPendiente(VentaRequestDTO dto, Cliente cliente,
                                                   BigDecimal total, String username,
                                                   Configuracion config) {
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now());
        venta.setFormaPago(dto.getFormaPago());
        venta.setEstado("pendiente_aprobacion");
        venta.setTotal(total.doubleValue());
        Venta guardada = ventaRepository.save(venta);

        // Serializar detalles como JSON para procesarlos al aprobar
        String detallesJson;
        try {
            detallesJson = objectMapper.writeValueAsString(dto.getDetalles());
        } catch (Exception e) {
            throw new ValidacionException("Error al procesar los detalles de la venta");
        }

        Usuario solicitante = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario solicitante no encontrado"));

        AprobacionVenta aprobacion = new AprobacionVenta();
        aprobacion.setVenta(guardada);
        aprobacion.setSolicitante(solicitante);
        aprobacion.setFechaSolicitud(LocalDateTime.now());
        aprobacion.setEstado("pendiente");
        aprobacion.setMontoTotal(total);
        aprobacion.setDetallesJson(detallesJson);
        aprobacionVentaRepository.save(aprobacion);

        // Notificar a todos los ADMIN
        String urlAccion = "/ventas/aprobaciones/" + guardada.getId();
        notificacionService.notificarAdmins(
                "Venta #" + guardada.getId() + " requiere aprobacion",
                String.format("El usuario %s creo una venta por $%,.2f que supera el umbral de $%,.2f.",
                        username, total, config.getUmbralAprobacionVenta()),
                "warning",
                urlAccion
        );

        auditoriaService.registrar(username, "CREAR_VENTA_PENDIENTE", "VENTAS",
                "Venta #" + guardada.getId() + " creada pendiente de aprobacion por $" + total);

        return toResponseDTO(guardada);
    }

    /**
     * Flujo normal — los triggers de BD manejan stock y cálculo de precios.
     */
    private VentaResponseDTO crearVentaNormal(VentaRequestDTO dto, Cliente cliente, String username) {
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now());
        venta.setFormaPago(dto.getFormaPago());
        venta.setEstado("completada");
        venta.setTotal(0.0);
        Venta guardada = ventaRepository.save(venta);

        dto.getDetalles().forEach(d -> {
            Bicicleta bicicleta = bicicletaRepository.findById(d.getCodigoBicicleta()).get();

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(guardada);
            detalle.setBicicleta(bicicleta);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(BigDecimal.ZERO);
            detalle.setSubtotal(BigDecimal.ZERO);
            detalleVentaRepository.save(detalle);
        });

        // Recargar venta (triggers actualizaron total y stock)
        Venta recargada = ventaRepository.findById(guardada.getId()).get();

        // Verificar stock minimo para cada bicicleta vendida
        dto.getDetalles().forEach(d ->
                bicicletaRepository.findById(d.getCodigoBicicleta())
                        .ifPresent(notificacionService::notificarStockBajo));

        auditoriaService.registrar(username, "CREAR_VENTA", "VENTAS",
                "Venta #" + guardada.getId() + " completada");

        return toResponseDTO(recargada);
    }

    @Transactional
    public VentaResponseDTO aprobar(Integer id, String aprobadorUsername) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe venta con id " + id));

        if (!"pendiente_aprobacion".equals(venta.getEstado()))
            throw new ValidacionException("La venta no esta en estado pendiente de aprobacion");

        AprobacionVenta aprobacion = aprobacionVentaRepository.findByVentaId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe registro de aprobacion para la venta " + id));

        // Parsear detalles
        List<DetalleVentaRequestDTO> detalles;
        try {
            detalles = objectMapper.readValue(aprobacion.getDetallesJson(),
                    new TypeReference<List<DetalleVentaRequestDTO>>() {});
        } catch (Exception e) {
            throw new ValidacionException("Error al leer los detalles de la venta pendiente");
        }

        // Validar stock antes de crear detalles
        for (DetalleVentaRequestDTO d : detalles) {
            Bicicleta b = bicicletaRepository.findById(d.getCodigoBicicleta())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe bicicleta con codigo " + d.getCodigoBicicleta()));
            if (b.getCantidad() < d.getCantidad()) {
                throw new ValidacionException(
                        "Stock insuficiente para " + b.getMarca() + " " + b.getModelo() +
                        ". Disponible: " + b.getCantidad() + ", Requerido: " + d.getCantidad());
            }
        }

        // Crear detalles: triggers descuentan stock y actualizan total
        detalles.forEach(d -> {
            Bicicleta bicicleta = bicicletaRepository.findById(d.getCodigoBicicleta()).get();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setBicicleta(bicicleta);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(BigDecimal.ZERO);
            detalle.setSubtotal(BigDecimal.ZERO);
            detalleVentaRepository.save(detalle);
        });

        venta.setEstado("completada");
        ventaRepository.save(venta);

        // Actualizar aprobacion
        Usuario aprobador = usuarioRepository.findByUsername(aprobadorUsername)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario aprobador no encontrado"));
        aprobacion.setAprobador(aprobador);
        aprobacion.setFechaResolucion(LocalDateTime.now());
        aprobacion.setEstado("aprobada");
        aprobacionVentaRepository.save(aprobacion);

        // Verificar stock minimo
        detalles.forEach(d ->
                bicicletaRepository.findById(d.getCodigoBicicleta())
                        .ifPresent(notificacionService::notificarStockBajo));

        auditoriaService.registrar(aprobadorUsername, "APROBAR_VENTA", "VENTAS",
                "Venta #" + id + " aprobada");

        return toResponseDTO(ventaRepository.findById(id).get());
    }

    @Transactional
    public VentaResponseDTO rechazar(Integer id, String observacion, String aprobadorUsername) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe venta con id " + id));

        if (!"pendiente_aprobacion".equals(venta.getEstado()))
            throw new ValidacionException("La venta no esta en estado pendiente de aprobacion");

        AprobacionVenta aprobacion = aprobacionVentaRepository.findByVentaId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe registro de aprobacion para la venta " + id));

        venta.setEstado("rechazada");
        ventaRepository.save(venta);

        Usuario aprobador = usuarioRepository.findByUsername(aprobadorUsername)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario aprobador no encontrado"));
        aprobacion.setAprobador(aprobador);
        aprobacion.setFechaResolucion(LocalDateTime.now());
        aprobacion.setEstado("rechazada");
        aprobacion.setObservacion(observacion);
        aprobacionVentaRepository.save(aprobacion);

        auditoriaService.registrar(aprobadorUsername, "RECHAZAR_VENTA", "VENTAS",
                "Venta #" + id + " rechazada. Motivo: " + observacion);

        return toResponseDTO(venta);
    }

    @Transactional
    public VentaResponseDTO cancelar(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe venta con id " + id));

        if ("devuelta".equals(venta.getEstado()))
            throw new ValidacionException("La venta ya fue cancelada");

        venta.setEstado("devuelta");
        return toResponseDTO(ventaRepository.save(venta));
    }

    public List<VentaResponseDTO> listarPendientes() {
        return ventaRepository.findByEstado("pendiente_aprobacion")
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public long contarPendientes() {
        return ventaRepository.countByEstado("pendiente_aprobacion");
    }

    public List<VentaResponseDTO> buscarPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaBetween(inicio, fin)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
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
                BigDecimal.valueOf(v.getTotal()),
                v.getFormaPago(),
                v.getEstado(),
                detalles
        );
    }
}
