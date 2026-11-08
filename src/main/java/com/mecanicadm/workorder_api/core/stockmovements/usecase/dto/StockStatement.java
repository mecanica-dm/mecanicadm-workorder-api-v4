package com.mecanicadm.workorder_api.core.stockmovements.usecase.dto;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;

import java.util.List;
import java.util.UUID;

public record StockStatement(
        UUID materialId,
        Integer currentBalance,
        List<StockMovements> movements
) {
}
