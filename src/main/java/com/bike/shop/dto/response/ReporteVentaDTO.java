package com.bike.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentaDTO {

    private Integer id;
    private LocalDateTime fecha;
    private String cliente;
    private BigDecimal total;
    private String estado;
    private String formaPago;
}
