package com.mecanicadm.workorder_api.core.workorder.usecase.command;

import java.util.UUID;

public record CalculateWorkOrderBudgetCommand(
        UUID workOrderId
) {
}
