package com.mecanicadm.workorder_api.infra.features.workorder.amqp.command;

import com.mecanicadm.workorder_api.core.workorder.usecase.command.AuthorizeWorkOrderCommand;

import java.util.UUID;

public record AuthorizeWorkOrderIntegrationCommand(UUID workOrderId) {

    public AuthorizeWorkOrderCommand toCommand() {
        return new AuthorizeWorkOrderCommand(workOrderId);
    }
}
