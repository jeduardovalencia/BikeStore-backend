package com.bike.shop.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BicicletaResponseDTO {
    private Integer codigo;
    private String marca;
    private String modelo;
    private String tipo;
    private BigDecimal precioCosto;
    private BigDecimal precioVenta;
    private Integer cantidad;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private String descripcion;
    private String imagen;
}