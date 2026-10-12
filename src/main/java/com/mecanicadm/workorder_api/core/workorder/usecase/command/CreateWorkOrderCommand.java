package com.mecanicadm.workorder_api.core.workorder.usecase.command;

import java.util.List;
import java.util.UUID;

public record CreateWorkOrderCommand(
        UUID clientId,
        String vehicleId,
        String description,
        List<UUID> laborIds,
        List<CreateWorkOrderMaterialItemCommand> materialItems
) {
}
