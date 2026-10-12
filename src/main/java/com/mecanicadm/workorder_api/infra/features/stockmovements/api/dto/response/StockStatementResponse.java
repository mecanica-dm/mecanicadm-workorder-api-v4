package com.mecanicadm.workorder_api.infra.features.stockmovements.api.dto.response;

import com.mecanicadm.workorder_api.core.stockmovements.usecase.dto.StockStatement;

import java.util.List;
import java.util.UUID;

public record StockStatementResponse(
        UUID materialId,
        Integer currentBalance,
        List<StockMovementResponse> movements
) {
    public static StockStatementResponse from(StockStatement statement) {
        return new StockStatementResponse(
                statement.materialId(),
                statement.currentBalance(),
                statement.movements().stream().map(StockMovementResponse::from).toList()
        );
    }
}
