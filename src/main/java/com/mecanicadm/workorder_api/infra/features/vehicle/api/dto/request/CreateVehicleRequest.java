package com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.request;

import com.mecanicadm.workorder_api.shared.validation.annotation.LicensePlate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateVehicleRequest(
        @NotBlank(message = "{validation.vehicle.model.not.blank}")
        String model,

        @NotBlank(message = "{validation.vehicle.licensePlate.not.blank}")
        @LicensePlate
        String licensePlate,

        @NotBlank(message = "{validation.vehicle.brand.not.blank}")
        String brand,

        @NotNull(message = "{validation.vehicle.modelYear.not.null}")
        Short modelYear
) {
}

