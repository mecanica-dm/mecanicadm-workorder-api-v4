package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateWorkOrderLaborItemRequest(
        @NotNull(message = "{validation.workorder.laborId.required}")
        UUID laborId
) {
}
