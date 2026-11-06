package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishLaborExecutionCommand;

public class FinishLaborExecutionUseCase implements VoidUseCase<FinishLaborExecutionCommand> {
    private final WorkOrderGateway gateway;
    private final WorkOrderLaborItemGateway laborItemGateway;

    public FinishLaborExecutionUseCase(WorkOrderGateway gateway, WorkOrderLaborItemGateway laborItemGateway) {
        this.gateway = gateway;
        this.laborItemGateway = laborItemGateway;
    }

    public void execute(FinishLaborExecutionCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.findLaborItem(cmd.laborItemId())
                .orElseThrow(WorkOrderExceptions.LaborItemNotFound::new)
                .finishExecution();

        WorkOrderLaborItem laborItem = workOrder.findLaborItem(cmd.laborItemId())
                .orElseThrow(WorkOrderExceptions.LaborItemNotFound::new);

        laborItemGateway.update(laborItem);
        gateway.update(workOrder);
    }
}
