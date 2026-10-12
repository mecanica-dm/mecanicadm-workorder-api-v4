package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import jakarta.validation.constraints.NotNull;

public record DecideWorkOrderBudgetRequest(
        @NotNull(message = "{validation.work.order.budget.decision.required}")
        WorkOrderBudgetStatus decision,

        String observation
) {
}
