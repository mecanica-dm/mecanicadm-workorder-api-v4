package com.mecanicadm.workorder_api.core.workorder.usecase.command;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public record UpdateWorkOrderCommand(
        @JsonIgnore
        UUID id,
        String vehicleId,
        UUID clientId,
        String description
) {
}
