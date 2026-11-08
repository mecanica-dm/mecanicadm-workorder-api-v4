package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderCanceledEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CancelWorkOrderCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import java.util.UUID;

public class CancelWorkOrderUseCase implements VoidUseCase<CancelWorkOrderCommand> {
    private final WorkOrderGateway gateway;
    private final StockMovementsGateway stockMovementsGateway;
    private final MessagePublisher publisher;

    public CancelWorkOrderUseCase(WorkOrderGateway gateway,
                                  StockMovementsGateway stockMovementsGateway,
                                  MessagePublisher publisher) {
        this.gateway = gateway;
        this.stockMovementsGateway = stockMovementsGateway;
        this.publisher = publisher;
    }

    @Override
    public void execute(CancelWorkOrderCommand command) {
        WorkOrder workOrder = gateway.findById(command.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        if (workOrder.isCancelled()) {
            return;
        }

        workOrder.cancel();
        workOrder.getMaterialItems().forEach(item -> releaseStock(workOrder.getId(), item.getMaterialId()));

        gateway.update(workOrder);
        publisher.publish(workOrder.getId(), WorkOrderCanceledEvent.of(workOrder));
    }

    private void releaseStock(UUID workOrderId, UUID materialId) {
        stockMovementsGateway.findAllByMaterialIdAndWorkOrderId(materialId, workOrderId).forEach(stock -> {
            stock.delete();
            stockMovementsGateway.update(stock);
        });
    }
}
