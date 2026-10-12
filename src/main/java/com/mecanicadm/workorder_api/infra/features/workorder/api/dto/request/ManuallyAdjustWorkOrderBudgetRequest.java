package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ManuallyAdjustWorkOrderBudgetRequest(
        @NotNull(message = "{validation.workorder.budget.price.required}")
        @PositiveOrZero(message = "{validation.workorder.budget.price.positiveorzero}")
        BigDecimal newTotalPrice
) {
}
