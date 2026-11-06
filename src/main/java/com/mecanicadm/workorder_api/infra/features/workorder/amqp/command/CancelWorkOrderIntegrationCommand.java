package com.mecanicadm.workorder_api.infra.features.workorder.amqp.command;

import com.mecanicadm.workorder_api.core.workorder.usecase.command.CancelWorkOrderCommand;

import java.util.UUID;

public record CancelWorkOrderIntegrationCommand(UUID workOrderId) {

    public CancelWorkOrderCommand toCommand() {
        return new CancelWorkOrderCommand(workOrderId);
    }
}
