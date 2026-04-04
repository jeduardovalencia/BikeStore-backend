package com.bike.shop.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BicicletaRequestDTO {
    private String marca;
    private String modelo;
    private String tipo;
    private BigDecimal precioCosto;
    private BigDecimal precioVenta;
    private String descripcion;
    private Integer cantidad;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private String imagen;
}