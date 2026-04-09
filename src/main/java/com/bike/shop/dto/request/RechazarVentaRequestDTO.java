package com.bike.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RechazarVentaRequestDTO {

    @NotBlank(message = "La observacion es obligatoria para rechazar una venta")
    private String observacion;
}
