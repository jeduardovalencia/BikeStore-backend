// PedidoResponseDTO
package com.bike.shop.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Integer id;
    private Integer idProveedor;
    private String nombreProveedor;
    private LocalDateTime fecha;
    private String estado;
    private List<DetallePedidoResponseDTO> detalles;
}