package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.ManuallyAdjustWorkOrderBudgetCommand;

public class ManuallyAdjustWorkOrderBudgetUseCase implements VoidUseCase<ManuallyAdjustWorkOrderBudgetCommand> {
    private final WorkOrderGateway gateway;

    public ManuallyAdjustWorkOrderBudgetUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(ManuallyAdjustWorkOrderBudgetCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        var budget = workOrder.getBudget()
                .orElseThrow(WorkOrderExceptions.BudgetNotFound::new);

        budget.updateTotalPrice(cmd.newTotalPrice());

        gateway.saveBudget(budget);
    }
}
