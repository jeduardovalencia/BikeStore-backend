// ProveedorResponseDTO
package com.bike.shop.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponseDTO {
    private Integer id;
    private String nombre;
    private String telefono;
    private String email;
    private String frecuenciaEntrega;
}