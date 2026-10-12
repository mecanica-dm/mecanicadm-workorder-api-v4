package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddLaborToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderLaborItemCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class AddLaborToWorkOrderUseCase implements VoidUseCase<AddLaborToWorkOrderCommand> {

    private final WorkOrderGateway gateway;
    private final WorkOrderLaborItemGateway laborItemGateway;
    private final CreateWorkOrderLaborItemUseCase createWorkOrderLaborItemUseCase;

    public AddLaborToWorkOrderUseCase(WorkOrderGateway gateway,
                                      WorkOrderLaborItemGateway laborItemGateway,
                                      CreateWorkOrderLaborItemUseCase createWorkOrderLaborItemUseCase) {
        this.gateway = gateway;
        this.laborItemGateway = laborItemGateway;
        this.createWorkOrderLaborItemUseCase = createWorkOrderLaborItemUseCase;
    }

    @Override
    public void execute(AddLaborToWorkOrderCommand cmd) {
        if (!gateway.existsById(cmd.workOrderId())) {
            throw new WorkOrderExceptions.NotFound();
        }

        WorkOrderLaborItem laborItem = createWorkOrderLaborItemUseCase.execute(new CreateWorkOrderLaborItemCommand(cmd.laborId(), cmd.workOrderId()));
        laborItemGateway.create(laborItem);
    }
}
