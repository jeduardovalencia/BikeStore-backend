// ProveedorRequestDTO
package com.bike.shop.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorRequestDTO {
    private String nombre;
    private String telefono;
    private String email;
    private String frecuenciaEntrega;
}