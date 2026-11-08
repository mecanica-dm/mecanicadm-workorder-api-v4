package com.mecanicadm.workorder_api.core.material.usecase.command;

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;

import java.math.BigDecimal;

public record CreateMaterialCommand(
        String name,
        String brand,
        String description,
        BigDecimal price,
        MaterialType type,
        int quantity
) {
}
