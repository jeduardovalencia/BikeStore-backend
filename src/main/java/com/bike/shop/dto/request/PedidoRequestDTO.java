// PedidoRequestDTO
package com.bike.shop.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {
    private Integer idProveedor;
    private List<DetallePedidoRequestDTO> detalles;
}