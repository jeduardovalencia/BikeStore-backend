// DetallePedidoResponseDTO
package com.bike.shop.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoResponseDTO {
    private Integer id;
    private Integer codigoBicicleta;
    private String marcaModelo;
    private Integer cantidad;
    private BigDecimal precioCostoUnitario;
}