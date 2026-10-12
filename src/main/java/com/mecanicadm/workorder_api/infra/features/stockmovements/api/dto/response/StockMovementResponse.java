package com.mecanicadm.workorder_api.infra.features.stockmovements.api.dto.response;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.enums.MovementType;

import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID workOrderId,
        Integer quantity,
        MovementType type
) {
    public static StockMovementResponse from(StockMovements movement) {
        return new StockMovementResponse(
                movement.getId(),
                movement.getWorkOrderId(),
                movement.getQuantity(),
                movement.getType()
        );
    }
}
