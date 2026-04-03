// VentaRequestDTO
package com.bike.shop.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDTO {
    private String documentoCliente;
    private String formaPago;
    private List<DetalleVentaRequestDTO> detalles;
}