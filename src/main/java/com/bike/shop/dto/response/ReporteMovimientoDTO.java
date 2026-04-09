package com.bike.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMovimientoDTO {

    private LocalDateTime fecha;
    private String producto;
    private String tipoMovimiento; // ENTRADA / SALIDA
    private Integer cantidad;
    private String referencia; // e.g. "Venta #5" o "Pedido #12"
}
