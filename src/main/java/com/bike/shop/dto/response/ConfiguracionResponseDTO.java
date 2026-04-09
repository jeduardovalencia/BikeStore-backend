package com.bike.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionResponseDTO {

    private Integer id;
    private String nombreTienda;
    private BigDecimal umbralAprobacionVenta;
    private BigDecimal umbralAprobacionPedido;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
}
