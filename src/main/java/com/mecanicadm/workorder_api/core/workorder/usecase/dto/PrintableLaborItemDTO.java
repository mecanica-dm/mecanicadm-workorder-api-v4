package com.mecanicadm.workorder_api.core.workorder.usecase.dto;

import java.math.BigDecimal;

public record PrintableLaborItemDTO(
        String description,
        BigDecimal price
) {}
