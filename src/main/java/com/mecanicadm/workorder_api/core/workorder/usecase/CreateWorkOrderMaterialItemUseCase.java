package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.exception.MaterialExceptions;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.DeductStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.DeductStockCommand;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderMaterialItemCommand;

public class CreateWorkOrderMaterialItemUseCase implements UseCase<CreateWorkOrderMaterialItemCommand, WorkOrderMaterialItem> {
    private final MaterialGateway materialGateway;
    private final DeductStockUseCase deductStockUseCase;

    public CreateWorkOrderMaterialItemUseCase(MaterialGateway materialGateway, DeductStockUseCase deductStockUseCase) {
        this.materialGateway = materialGateway;
        this.deductStockUseCase = deductStockUseCase;
    }

    public WorkOrderMaterialItem execute(CreateWorkOrderMaterialItemCommand cmd) {
        Material material = materialGateway.findById(cmd.materialId())
                .orElseThrow(MaterialExceptions.MaterialNotFound::new);

        deductStockUseCase.execute(new DeductStockCommand(material.getId(), cmd.workOrderId(), cmd.quantity()));

        return WorkOrderMaterialItem.create(material.getId(), cmd.quantity());
    }
}
