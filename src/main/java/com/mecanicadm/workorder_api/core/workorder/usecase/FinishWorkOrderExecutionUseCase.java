package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishWorkOrderExecutionCommand;

public class FinishWorkOrderExecutionUseCase implements VoidUseCase<FinishWorkOrderExecutionCommand> {
    private final WorkOrderGateway gateway;

    public FinishWorkOrderExecutionUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(FinishWorkOrderExecutionCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.markAsExecutionCompleted();
        gateway.update(workOrder);
    }
}
