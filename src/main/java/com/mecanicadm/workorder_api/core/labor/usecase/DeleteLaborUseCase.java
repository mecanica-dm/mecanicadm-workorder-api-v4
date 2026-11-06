package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.core.labor.usecase.command.DeleteLaborCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class DeleteLaborUseCase implements VoidUseCase<DeleteLaborCommand> {

    private final LaborGateway gateway;

    public DeleteLaborUseCase(LaborGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(DeleteLaborCommand command) {
        Labor labor = gateway.findById(command.id())
                .orElseThrow(LaborExceptions.LaborNotFound::new);
        labor.softDelete();
        gateway.update(labor);
    }
}
