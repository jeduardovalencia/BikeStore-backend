package com.bike.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteInventarioDTO {

    private Integer codigo;
    private String marca;
    private String modelo;
    private String tipo;
    private Integer stockActual;
    private Integer stockMinimo;
    private BigDecimal precioVenta;
}
