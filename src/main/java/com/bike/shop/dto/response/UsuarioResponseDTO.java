package com.bike.shop.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Integer id;
    private String username;
    private String nombre;
    private String email;
    private Boolean estado;
    private Integer rolId;
    private String rolNombre;
}
