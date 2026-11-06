package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderVehicleDeliveredEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DeliverWorkOrderCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class DeliverWorkOrderUseCase implements VoidUseCase<DeliverWorkOrderCommand> {
    private final WorkOrderGateway gateway;
    private final MessagePublisher publisher;

    public DeliverWorkOrderUseCase(WorkOrderGateway gateway, MessagePublisher publisher) {
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void execute(DeliverWorkOrderCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.markAsDelivered();

        gateway.update(workOrder);
        publisher.publish(workOrder.getId(), WorkOrderVehicleDeliveredEvent.of(workOrder));
    }

}
