package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.usecase.command.CreateLaborCommand;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.UUID;

public class CreateLaborUseCase implements UseCase<CreateLaborCommand, UUID> {

    private final LaborGateway gateway;

    public CreateLaborUseCase(LaborGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public UUID execute(CreateLaborCommand command) {
        Labor created = gateway.create(Labor.create(command.name(), command.price()));
        return created.getId();
    }
}
