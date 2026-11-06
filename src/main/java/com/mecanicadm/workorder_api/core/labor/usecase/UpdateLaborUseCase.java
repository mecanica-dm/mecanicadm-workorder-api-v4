package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.core.labor.usecase.command.UpdateLaborCommand;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.UUID;

public class UpdateLaborUseCase implements UseCase<UpdateLaborCommand, UUID> {

    private final LaborGateway gateway;

    public UpdateLaborUseCase(LaborGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public UUID execute(UpdateLaborCommand command) {
        Labor labor = gateway.findById(command.id())
                .orElseThrow(LaborExceptions.LaborNotFound::new);

        labor.update(command.name(), command.price());
        return gateway.update(labor).getId();
    }
}
