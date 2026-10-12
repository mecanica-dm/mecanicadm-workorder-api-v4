package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartWorkOrderExecutionCommand;

public class StartWorkOrderExecutionUseCase implements VoidUseCase<StartWorkOrderExecutionCommand> {
    private final WorkOrderGateway gateway;

    public StartWorkOrderExecutionUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(StartWorkOrderExecutionCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.markAsInExecution();
        gateway.update(workOrder);
    }
}
