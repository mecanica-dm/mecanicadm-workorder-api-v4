package com.mecanicadm.workorder_api.core.workorder.usecase.command;

import java.util.UUID;

public record RemoveLaborItemFromWorkOrderCommand(
        UUID workOrderId,
        UUID laborItemId
) {
}
