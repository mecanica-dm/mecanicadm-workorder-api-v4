package com.mecanicadm.workorder_api.infra.features.labor.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateLaborRequest(
        @NotBlank(message = "{validation.labor.name.not.blank}")
        String name,

        @NotNull(message = "{validation.labor.price.not.null}")
        BigDecimal price
) {
}

