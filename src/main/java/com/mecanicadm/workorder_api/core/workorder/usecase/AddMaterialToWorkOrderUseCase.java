package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderUpdatedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddMaterialToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderMaterialItemCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import java.util.UUID;

public class AddMaterialToWorkOrderUseCase implements VoidUseCase<AddMaterialToWorkOrderCommand> {
    private final WorkOrderGateway gateway;
    private final WorkOrderMaterialItemGateway materialItemGateway;
    private final CreateWorkOrderMaterialItemUseCase createWorkOrderMaterialItemUseCase;
    private final MessagePublisher publisher;

    public AddMaterialToWorkOrderUseCase(WorkOrderGateway gateway,
                                         WorkOrderMaterialItemGateway materialItemGateway,
                                         CreateWorkOrderMaterialItemUseCase createWorkOrderMaterialItemUseCase,
                                         MessagePublisher publisher) {
        this.gateway = gateway;
        this.materialItemGateway = materialItemGateway;
        this.createWorkOrderMaterialItemUseCase = createWorkOrderMaterialItemUseCase;
        this.publisher = publisher;
    }

    @Override
    public void execute(AddMaterialToWorkOrderCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        workOrder.ensureEditable();

        attach(cmd.workOrderId(), cmd.materialId(), cmd.quantity());

        publishUpdated(cmd.workOrderId());
    }

    public void attach(UUID workOrderId, UUID materialId, int quantity) {
        createWorkOrderMaterialItemUseCase.execute(
                new CreateWorkOrderMaterialItemCommand(workOrderId, materialId, quantity));

        materialItemGateway.findByWorkOrderIdAndMaterialId(workOrderId, materialId)
                .ifPresentOrElse(
                        existing -> {
                            existing.addQuantity(quantity);
                            materialItemGateway.update(existing, workOrderId);
                        },
                        () -> {
                            WorkOrderMaterialItem materialItem = WorkOrderMaterialItem.create(materialId, quantity);
                            materialItemGateway.create(materialItem, workOrderId);
                        }
                );
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
