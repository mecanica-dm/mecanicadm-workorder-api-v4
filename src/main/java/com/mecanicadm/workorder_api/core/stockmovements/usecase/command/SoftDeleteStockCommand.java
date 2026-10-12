package com.mecanicadm.workorder_api.core.stockmovements.usecase.command;

import java.util.UUID;

public record SoftDeleteStockCommand(UUID materialId, UUID workOrderId) {
}
