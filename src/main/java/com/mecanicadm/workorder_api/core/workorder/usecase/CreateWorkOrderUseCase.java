package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderCreatedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderCommand;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.UUID;

public class CreateWorkOrderUseCase implements UseCase<CreateWorkOrderCommand, UUID> {
    private final WorkOrderGateway gateway;
    private final AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;
    private final AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;
    private final MessagePublisher publisher;

    public CreateWorkOrderUseCase(WorkOrderGateway gateway,
                                  AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase,
                                  AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase,
                                  MessagePublisher publisher) {
        this.gateway = gateway;
        this.addLaborToWorkOrderUseCase = addLaborToWorkOrderUseCase;
        this.addMaterialToWorkOrderUseCase = addMaterialToWorkOrderUseCase;
        this.publisher = publisher;
    }

    public UUID execute(CreateWorkOrderCommand cmd) {
        WorkOrder workOrder = WorkOrder.create(cmd.clientId(), cmd.vehicleId(), cmd.description());
        gateway.create(workOrder);

        UUID workOrderId = workOrder.getId();

        if (cmd.laborIds() != null) {
            cmd.laborIds().forEach(laborId -> addLaborToWorkOrderUseCase.attach(workOrderId, laborId));
        }

        if (cmd.materialItems() != null) {
            cmd.materialItems().forEach(materialItem ->
                    addMaterialToWorkOrderUseCase.attach(workOrderId, materialItem.materialId(), materialItem.quantity()));
        }

        WorkOrderDetails details = gateway.findDetailsById(workOrderId)
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        publisher.publish(details.id(), WorkOrderCreatedEvent.of(details));

        return workOrderId;
    }
}
