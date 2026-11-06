package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderChangesRequestedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RequestWorkOrderChangesCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class RequestWorkOrderChangesUseCase implements VoidUseCase<RequestWorkOrderChangesCommand> {

    private final WorkOrderGateway gateway;
    private final MessagePublisher publisher;

    public RequestWorkOrderChangesUseCase(WorkOrderGateway gateway, MessagePublisher publisher) {
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void execute(RequestWorkOrderChangesCommand command) {
        WorkOrder workOrder = gateway.findById(command.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.markAsChangesRequested(command.observation());

        gateway.update(workOrder);
        publisher.publish(workOrder.getId(), WorkOrderChangesRequestedEvent.of(workOrder, command.observation()));
    }
}
