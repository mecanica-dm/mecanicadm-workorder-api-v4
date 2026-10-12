package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.UpdateWorkOrderCommand;

public class UpdateWorkOrderUseCase implements VoidUseCase<UpdateWorkOrderCommand> {
    private final WorkOrderGateway gateway;

    public UpdateWorkOrderUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(UpdateWorkOrderCommand cmd) {
        var workOrder = gateway.findById(cmd.id())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.update(cmd.clientId(), cmd.vehicleId(), cmd.description());

        gateway.update(workOrder);
    }
}
