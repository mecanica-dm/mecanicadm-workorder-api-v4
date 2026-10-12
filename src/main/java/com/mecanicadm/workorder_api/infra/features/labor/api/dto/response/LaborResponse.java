package com.mecanicadm.workorder_api.infra.features.labor.api.dto.response;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;

import java.math.BigDecimal;
import java.util.UUID;

public record LaborResponse(
        UUID id,
        String name,
        BigDecimal price
) {
    public static LaborResponse from(Labor labor) {
        return new LaborResponse(
                labor.getId(),
                labor.getName(),
                labor.getPrice()
        );
    }
}

