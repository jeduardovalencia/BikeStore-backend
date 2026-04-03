// DetalleVentaRequestDTO
package com.bike.shop.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaRequestDTO {
    private Integer codigoBicicleta;
    private Integer cantidad;
}