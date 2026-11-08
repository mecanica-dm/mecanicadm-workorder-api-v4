package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderExecutionStartedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartWorkOrderExecutionCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class StartWorkOrderExecutionUseCase implements VoidUseCase<StartWorkOrderExecutionCommand> {
    private final WorkOrderGateway gateway;
    private final MessagePublisher publisher;

    public StartWorkOrderExecutionUseCase(WorkOrderGateway gateway, MessagePublisher publisher) {
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void execute(StartWorkOrderExecutionCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.markAsInExecution();

        gateway.update(workOrder);
        publisher.publish(workOrder.getId(), WorkOrderExecutionStartedEvent.of(workOrder));
    }

}
