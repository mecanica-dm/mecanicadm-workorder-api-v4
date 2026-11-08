package com.mecanicadm.workorder_api.core.material.usecase.query;

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;

public record SearchMaterialsQuery(
        String name,
        String brand,
        MaterialType type,
        int page,
        int size,
        String sortBy,
        String direction
) {
}
