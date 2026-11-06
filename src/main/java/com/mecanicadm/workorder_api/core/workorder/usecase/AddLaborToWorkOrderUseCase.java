package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderUpdatedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddLaborToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderLaborItemCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import java.util.UUID;

public class AddLaborToWorkOrderUseCase implements VoidUseCase<AddLaborToWorkOrderCommand> {

    private final WorkOrderGateway gateway;
    private final WorkOrderLaborItemGateway laborItemGateway;
    private final CreateWorkOrderLaborItemUseCase createWorkOrderLaborItemUseCase;
    private final MessagePublisher publisher;

    public AddLaborToWorkOrderUseCase(WorkOrderGateway gateway,
                                      WorkOrderLaborItemGateway laborItemGateway,
                                      CreateWorkOrderLaborItemUseCase createWorkOrderLaborItemUseCase,
                                      MessagePublisher publisher) {
        this.gateway = gateway;
        this.laborItemGateway = laborItemGateway;
        this.createWorkOrderLaborItemUseCase = createWorkOrderLaborItemUseCase;
        this.publisher = publisher;
    }

    @Override
    public void execute(AddLaborToWorkOrderCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        workOrder.ensureEditable();

        attach(cmd.workOrderId(), cmd.laborId());

        publishUpdated(cmd.workOrderId());
    }

    public void attach(UUID workOrderId, UUID laborId) {
        WorkOrderLaborItem laborItem = createWorkOrderLaborItemUseCase.execute(
                new CreateWorkOrderLaborItemCommand(laborId, workOrderId));
        laborItemGateway.create(laborItem);
    }

    private void publishUpdated(UUID workOrderId) {
        WorkOrder current = gateway.findById(workOrderId)
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        gateway.update(current);
        WorkOrderDetails details = gateway.findDetailsById(current.getId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        publisher.publish(details.id(), WorkOrderUpdatedEvent.of(details));
    }
}
