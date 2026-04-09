package com.bike.shop.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class ConfiguracionRequestDTO {

    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String nombreTienda;

    @NotNull(message = "El umbral de aprobacion de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El umbral debe ser mayor a 0")
    private BigDecimal umbralAprobacionVenta;

    @NotNull(message = "El umbral de aprobacion de pedido es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El umbral debe ser mayor a 0")
    private BigDecimal umbralAprobacionPedido;

    private LocalTime horarioInicio;
    private LocalTime horarioFin;
}
