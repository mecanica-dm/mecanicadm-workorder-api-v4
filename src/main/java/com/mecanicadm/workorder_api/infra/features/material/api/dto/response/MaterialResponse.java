package com.mecanicadm.workorder_api.infra.features.material.api.dto.response;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;

import java.math.BigDecimal;
import java.util.UUID;

public record MaterialResponse(
        UUID id,
        String name,
        String brand,
        String description,
        BigDecimal price,
        MaterialType type
) {
    public static MaterialResponse from(Material material) {
        return new MaterialResponse(
                material.getId(),
                material.getName(),
                material.getBrand(),
                material.getDescription(),
                material.getPrice(),
                material.getType()
        );
    }
}
