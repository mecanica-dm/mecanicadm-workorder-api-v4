package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderUpdatedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveLaborItemFromWorkOrderCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;


public class RemoveLaborItemFromWorkOrderUseCase implements VoidUseCase<RemoveLaborItemFromWorkOrderCommand> {
    private final WorkOrderLaborItemGateway gateway;
    private final WorkOrderGateway workOrderGateway;
    private final MessagePublisher publisher;

    public RemoveLaborItemFromWorkOrderUseCase(WorkOrderLaborItemGateway gateway,
                                               WorkOrderGateway workOrderGateway,
                                               MessagePublisher publisher) {
        this.gateway = gateway;
        this.workOrderGateway = workOrderGateway;
        this.publisher = publisher;
    }

    @Override
    public void execute(RemoveLaborItemFromWorkOrderCommand cmd) {
        WorkOrderLaborItem laborItem = gateway.findById(cmd.laborItemId())
                .orElseThrow(WorkOrderExceptions.LaborItemNotFound::new);

        WorkOrder workOrder = workOrderGateway.findById(laborItem.getWorkOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        workOrder.ensureEditable();
        gateway.delete(laborItem);

        publishUpdated(workOrder);
    }

    private void publishUpdated(WorkOrder workOrder) {
        WorkOrder current = workOrderGateway.findById(workOrder.getId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        workOrderGateway.update(current);
        WorkOrderDetails details = workOrderGateway.findDetailsById(current.getId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        publisher.publish(details.id(), WorkOrderUpdatedEvent.of(details));
    }
}