package com.bike.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoVentaDTO {

    private Integer id;
    private String estado;
    private LocalDateTime fecha;
    private String cliente;
    private BigDecimal total;
    private String observacion;
    private List<ProductoSeguimiento> productos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoSeguimiento {
        private String modelo;
        private String marca;
        private Integer cantidad;
        private BigDecimal precioUnitario;
    }
}
