package com.bike.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AprobacionVentaResponseDTO {

    private Integer id;
    private Integer idVenta;
    private Integer idSolicitante;
    private String nombreSolicitante;
    private Integer idAprobador;
    private String nombreAprobador;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaResolucion;
    private String estado;
    private String observacion;
    private BigDecimal montoTotal;
}
