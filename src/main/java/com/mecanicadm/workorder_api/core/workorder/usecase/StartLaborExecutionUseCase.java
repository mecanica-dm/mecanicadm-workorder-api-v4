package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartLaborExecutionCommand;

public class StartLaborExecutionUseCase implements VoidUseCase<StartLaborExecutionCommand> {
    private final WorkOrderGateway gateway;
    private final WorkOrderLaborItemGateway laborItemGateway;

    public StartLaborExecutionUseCase(WorkOrderGateway gateway, WorkOrderLaborItemGateway laborItemGateway) {
        this.gateway = gateway;
        this.laborItemGateway = laborItemGateway;
    }

    public void execute(StartLaborExecutionCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.startLaborItem(cmd.laborItemId());

        WorkOrderLaborItem laborItem = workOrder.findLaborItem(cmd.laborItemId())
                .orElseThrow(WorkOrderExceptions.LaborItemNotFound::new);

        laborItemGateway.update(laborItem);
        gateway.update(workOrder);
    }
}
