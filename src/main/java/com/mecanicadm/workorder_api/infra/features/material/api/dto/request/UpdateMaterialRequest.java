package com.mecanicadm.workorder_api.infra.features.material.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMaterialRequest(
        @JsonIgnore
        UUID id,

        @NotNull(message = "{validation.material.name.required}")
        String name,

        String brand,

        String description,

        @NotNull(message = "{validation.material.price.required}")
        @Positive(message = "{validation.material.price.positive}")
        BigDecimal price,

        @NotNull(message = "{validation.material.type.required}")
        MaterialType type
) {
    public UpdateMaterialRequest withId(UUID id) {
        return new UpdateMaterialRequest(id, name, brand, description, price, type);
    }
}
