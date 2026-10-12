package com.mecanicadm.workorder_api.core.workorder.usecase.dto;

import java.math.BigDecimal;

public record PrintableMaterialItemDTO(
        String materialName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {}
