package com.mecanicadm.workorder_api.core.workorder.usecase.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ManuallyAdjustWorkOrderBudgetCommand(
        UUID workOrderId,
        BigDecimal newTotalPrice
) {
}
