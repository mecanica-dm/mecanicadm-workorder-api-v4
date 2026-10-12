package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.usecase.SoftDeleteStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.SoftDeleteStockCommand;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveMaterialItemFromWorkOrderCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class RemoveMaterialItemFromWorkOrderUseCase implements VoidUseCase<RemoveMaterialItemFromWorkOrderCommand> {
    private final WorkOrderGateway workOrderGateway;
    private final WorkOrderMaterialItemGateway materialItemGateway;
    private final SoftDeleteStockUseCase softDeleteStockUseCase;

    public RemoveMaterialItemFromWorkOrderUseCase(WorkOrderGateway workOrderGateway,
                                                  WorkOrderMaterialItemGateway materialItemGateway,
                                                  SoftDeleteStockUseCase softDeleteStockUseCase) {
        this.workOrderGateway = workOrderGateway;
        this.materialItemGateway = materialItemGateway;
        this.softDeleteStockUseCase = softDeleteStockUseCase;
    }

    public void execute(RemoveMaterialItemFromWorkOrderCommand cmd) {
        if (!workOrderGateway.existsById(cmd.workOrderId())) {
            throw new WorkOrderExceptions.NotFound();
        }
        softDeleteStockUseCase.execute(new SoftDeleteStockCommand(cmd.materialId(), cmd.workOrderId()));
        materialItemGateway.deleteByWorkOrderIdAndMaterialId(cmd.workOrderId(), cmd.materialId());
    }
}
