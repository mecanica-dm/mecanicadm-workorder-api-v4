package com.mecanicadm.workorder_api.core.workorder.usecase.command;

import java.util.UUID;

public record RemoveMaterialItemFromWorkOrderCommand(
        UUID workOrderId,
        UUID materialId
) {
}
