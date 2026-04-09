package com.bike.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaResponseDTO {

    private Integer id;
    private Integer idUsuario;
    private String usernameUsuario;
    private String accion;
    private String modulo;
    private String descripcion;
    private LocalDateTime fecha;
}
