package com.mecanicadm.workorder_api.core.workorder.usecase.command;

import java.util.UUID;

public record CreateWorkOrderMaterialItemCommand(
        UUID workOrderId,
        UUID materialId,
        int quantity
) {
}
