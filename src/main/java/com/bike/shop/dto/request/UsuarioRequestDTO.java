package com.bike.shop.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    private String username;
    private String password;
    private String nombre;
    private String email;
    private Boolean estado;
    private Integer rolId;
}
