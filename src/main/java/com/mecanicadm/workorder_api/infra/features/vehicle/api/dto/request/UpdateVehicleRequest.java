package com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateVehicleRequest(
        @NotBlank(message = "{validation.vehicle.model.not.blank}")
        String model,

        @NotBlank(message = "{validation.vehicle.brand.not.blank}")
        String brand,

        @NotNull(message = "{validation.vehicle.modelYear.not.null}")
        Short modelYear
) {
}

