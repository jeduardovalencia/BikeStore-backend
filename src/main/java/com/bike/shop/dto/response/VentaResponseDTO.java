// VentaResponseDTO
package com.bike.shop.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {
    private Integer id;
    private String documentoCliente;
    private String nombreCliente;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String formaPago;
    private String estado;
    private List<DetalleVentaResponseDTO> detalles;
}