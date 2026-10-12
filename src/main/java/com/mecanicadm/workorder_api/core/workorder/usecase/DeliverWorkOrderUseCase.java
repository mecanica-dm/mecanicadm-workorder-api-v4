package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DeliverWorkOrderCommand;

public class DeliverWorkOrderUseCase implements VoidUseCase<DeliverWorkOrderCommand> {
    private final WorkOrderGateway gateway;

    public DeliverWorkOrderUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(DeliverWorkOrderCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.markAsDelivered();
        gateway.update(workOrder);
    }
}
