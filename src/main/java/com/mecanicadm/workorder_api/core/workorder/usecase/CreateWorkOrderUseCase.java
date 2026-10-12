package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddLaborToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddMaterialToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderCommand;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.UUID;

public class CreateWorkOrderUseCase implements UseCase<CreateWorkOrderCommand, UUID> {
    private final WorkOrderGateway gateway;
    private final AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;
    private final AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;

    public CreateWorkOrderUseCase(WorkOrderGateway gateway,
                                  AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase,
                                  AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase) {
        this.gateway = gateway;
        this.addLaborToWorkOrderUseCase = addLaborToWorkOrderUseCase;
        this.addMaterialToWorkOrderUseCase = addMaterialToWorkOrderUseCase;
    }

    public UUID execute(CreateWorkOrderCommand cmd) {
        WorkOrder workOrder = WorkOrder.create(cmd.clientId(), cmd.vehicleId(), cmd.description());
        gateway.create(workOrder);

        UUID workOrderId = workOrder.getId();

        if (cmd.laborIds() != null) {
            cmd.laborIds().forEach(laborId ->
                    addLaborToWorkOrderUseCase.execute(new AddLaborToWorkOrderCommand(workOrderId, laborId)));
        }

        if (cmd.materialItems() != null) {
            cmd.materialItems().forEach(materialItem ->
                    addMaterialToWorkOrderUseCase.execute(new AddMaterialToWorkOrderCommand(
                            workOrderId, materialItem.materialId(), materialItem.quantity())));
        }

        return workOrderId;
    }
}

