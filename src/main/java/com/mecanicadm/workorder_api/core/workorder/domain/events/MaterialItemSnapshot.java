package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;

import java.math.BigDecimal;
import java.util.UUID;

public record MaterialItemSnapshot(
        UUID id,
        UUID materialId,
        String name,
        String brand,
        String description,
        MaterialType type,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal totalPrice) {
}
