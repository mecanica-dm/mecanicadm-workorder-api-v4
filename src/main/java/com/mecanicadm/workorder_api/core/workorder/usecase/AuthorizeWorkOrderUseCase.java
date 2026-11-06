package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderAuthorizedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AuthorizeWorkOrderCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class AuthorizeWorkOrderUseCase implements VoidUseCase<AuthorizeWorkOrderCommand> {

    private final WorkOrderGateway gateway;
    private final MessagePublisher publisher;

    public AuthorizeWorkOrderUseCase(WorkOrderGateway gateway, MessagePublisher publisher) {
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void execute(AuthorizeWorkOrderCommand command) {
        WorkOrder workOrder = gateway.findById(command.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.markAsAwaitingExecution();

        gateway.update(workOrder);
        publisher.publish(workOrder.getId(), WorkOrderAuthorizedEvent.of(workOrder));
    }
}
