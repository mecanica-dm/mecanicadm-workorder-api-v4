package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderExecutionFinishedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishWorkOrderExecutionCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;


public class FinishWorkOrderExecutionUseCase implements VoidUseCase<FinishWorkOrderExecutionCommand> {
    private final WorkOrderGateway gateway;
    private final MessagePublisher publisher;

    public FinishWorkOrderExecutionUseCase(WorkOrderGateway gateway, MessagePublisher publisher) {
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void execute(FinishWorkOrderExecutionCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.markAsExecutionCompleted();

        gateway.update(workOrder);
        publisher.publish(workOrder.getId(), WorkOrderExecutionFinishedEvent.of(workOrder));
    }

}