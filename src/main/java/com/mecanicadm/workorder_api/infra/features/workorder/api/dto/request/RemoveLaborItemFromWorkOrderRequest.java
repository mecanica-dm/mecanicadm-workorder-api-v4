package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RemoveLaborItemFromWorkOrderRequest(
        @JsonIgnore
        UUID workOrderId,

        @NotNull(message = "{validation.workorder.laboritem.id.required}")
        UUID laborItemId
) {
}
