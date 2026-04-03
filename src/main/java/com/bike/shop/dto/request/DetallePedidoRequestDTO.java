// DetallePedidoRequestDTO
package com.bike.shop.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoRequestDTO {
    private Integer codigoBicicleta;
    private Integer cantidad;
    private BigDecimal precioCostoUnitario;
}