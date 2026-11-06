package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.SoftDeleteStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.SoftDeleteStockCommand;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderUpdatedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveMaterialItemFromWorkOrderCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;


public class RemoveMaterialItemFromWorkOrderUseCase implements VoidUseCase<RemoveMaterialItemFromWorkOrderCommand> {
    private final WorkOrderGateway workOrderGateway;
    private final WorkOrderMaterialItemGateway materialItemGateway;
    private final SoftDeleteStockUseCase softDeleteStockUseCase;
    private final MessagePublisher publisher;

    public RemoveMaterialItemFromWorkOrderUseCase(WorkOrderGateway workOrderGateway,
                                                  WorkOrderMaterialItemGateway materialItemGateway,
                                                  SoftDeleteStockUseCase softDeleteStockUseCase,
                                                  MessagePublisher publisher) {
        this.workOrderGateway = workOrderGateway;
        this.materialItemGateway = materialItemGateway;
        this.softDeleteStockUseCase = softDeleteStockUseCase;
        this.publisher = publisher;
    }

    @Override
    public void execute(RemoveMaterialItemFromWorkOrderCommand cmd) {
        WorkOrder workOrder = workOrderGateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);
        workOrder.ensureEditable();

        softDeleteStockUseCase.execute(new SoftDeleteStockCommand(cmd.materialId(), cmd.workOrderId()));
        materialItemGateway.deleteByWorkOrderIdAndMaterialId(cmd.workOrderId(), cmd.materialId());

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