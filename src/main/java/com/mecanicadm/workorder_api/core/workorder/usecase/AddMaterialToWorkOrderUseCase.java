package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddMaterialToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderMaterialItemCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class AddMaterialToWorkOrderUseCase implements VoidUseCase<AddMaterialToWorkOrderCommand> {
    private final WorkOrderGateway gateway;
    private final WorkOrderMaterialItemGateway materialItemGateway;
    private final CreateWorkOrderMaterialItemUseCase createWorkOrderMaterialItemUseCase;

    public AddMaterialToWorkOrderUseCase(WorkOrderGateway gateway,
                                         WorkOrderMaterialItemGateway materialItemGateway,
                                         CreateWorkOrderMaterialItemUseCase createWorkOrderMaterialItemUseCase) {
        this.gateway = gateway;
        this.materialItemGateway = materialItemGateway;
        this.createWorkOrderMaterialItemUseCase = createWorkOrderMaterialItemUseCase;
    }

    public void execute(AddMaterialToWorkOrderCommand cmd) {
        if (!gateway.existsById(cmd.workOrderId())) {
            throw new WorkOrderExceptions.NotFound();
        }

        createWorkOrderMaterialItemUseCase.execute(
                new CreateWorkOrderMaterialItemCommand(cmd.workOrderId(), cmd.materialId(), cmd.quantity()));

        materialItemGateway.findByWorkOrderIdAndMaterialId(cmd.workOrderId(), cmd.materialId())
                .ifPresentOrElse(
                        existing -> {
                            existing.addQuantity(cmd.quantity());
                            materialItemGateway.update(existing, cmd.workOrderId());
                        },
                        () -> {
                            WorkOrderMaterialItem materialItem = WorkOrderMaterialItem.create(cmd.materialId(), cmd.quantity());
                            materialItemGateway.create(materialItem, cmd.workOrderId());
                        }
                );
    }
}
