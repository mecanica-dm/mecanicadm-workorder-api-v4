package com.mecanicadm.workorder_api.infra.features.workorder.amqp.command;

import com.mecanicadm.workorder_api.core.workorder.usecase.command.RequestWorkOrderChangesCommand;

import java.util.UUID;

public record ChangesRequestedWorkOrderIntegrationCommand(UUID workOrderId, String observation) {

    public RequestWorkOrderChangesCommand toCommand() {
        return new RequestWorkOrderChangesCommand(workOrderId, observation);
    }
}
