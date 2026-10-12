package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.SoftDeleteWorkOrderCommand;

import java.util.UUID;

public class SoftDeleteWorkOrderUseCase implements VoidUseCase<SoftDeleteWorkOrderCommand> {
    private final WorkOrderGateway gateway;
    private final WorkOrderMaterialItemGateway materialItemGateway;
    private final StockMovementsGateway stockMovementsGateway;

    public SoftDeleteWorkOrderUseCase(WorkOrderGateway gateway,
                                       WorkOrderMaterialItemGateway materialItemGateway,
                                       StockMovementsGateway stockMovementsGateway) {
        this.gateway = gateway;
        this.materialItemGateway = materialItemGateway;
        this.stockMovementsGateway = stockMovementsGateway;
    }

    public void execute(SoftDeleteWorkOrderCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.id()).orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.getMaterialItems().forEach(item -> {
            softDeleteStockForItem(workOrder.getId(), item.getMaterialId());
            materialItemGateway.deleteByWorkOrderIdAndMaterialId(workOrder.getId(), item.getMaterialId());
        });

        workOrder.delete();
        gateway.update(workOrder);
    }

    private void softDeleteStockForItem(UUID workOrderId, UUID materialId) {
        stockMovementsGateway.findByMaterialIdAndWorkOrderId(materialId, workOrderId)
                .ifPresent(stock -> {
                    stock.delete();
                    stockMovementsGateway.update(stock);
                });
    }
}
