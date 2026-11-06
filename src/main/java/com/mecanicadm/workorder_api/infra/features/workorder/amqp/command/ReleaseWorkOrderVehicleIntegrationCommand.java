package com.mecanicadm.workorder_api.infra.features.workorder.amqp.command;

import com.mecanicadm.workorder_api.core.workorder.usecase.command.ReleaseWorkOrderVehicleCommand;

import java.util.UUID;

public record ReleaseWorkOrderVehicleIntegrationCommand(UUID workOrderId) {

    public ReleaseWorkOrderVehicleCommand toCommand() {
        return new ReleaseWorkOrderVehicleCommand(workOrderId);
    }
}
