package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderLaborItemCommand;

public class CreateWorkOrderLaborItemUseCase implements UseCase<CreateWorkOrderLaborItemCommand, WorkOrderLaborItem> {

    private final LaborGateway laborGateway;

    public CreateWorkOrderLaborItemUseCase(LaborGateway laborGateway) {
        this.laborGateway = laborGateway;
    }

    public WorkOrderLaborItem execute(CreateWorkOrderLaborItemCommand cmd) {
        if (!laborGateway.existsById(cmd.laborId())) {
            throw new LaborExceptions.LaborNotFound();
        }

        return WorkOrderLaborItem.create(cmd.laborId(), cmd.workOrderId());
    }
}
