package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddLaborToWorkOrderRequest(
        @JsonIgnore
        UUID workOrderId,

        @NotNull(message = "{validation.workorder.labor.id.required}")
        UUID laborId
) {
}
