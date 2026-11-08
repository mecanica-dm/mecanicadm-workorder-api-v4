package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderUpdatedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.UpdateWorkOrderCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;


public class UpdateWorkOrderUseCase implements VoidUseCase<UpdateWorkOrderCommand> {
    private final WorkOrderGateway gateway;
    private final MessagePublisher publisher;

    public UpdateWorkOrderUseCase(WorkOrderGateway gateway, MessagePublisher publisher) {
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void execute(UpdateWorkOrderCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.id())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.update(cmd.clientId(), cmd.vehicleId(), cmd.description());

        gateway.update(workOrder);
        WorkOrderDetails details = gateway.findDetailsById(workOrder.getId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        publisher.publish(details.id(), WorkOrderUpdatedEvent.of(details));
    }

}