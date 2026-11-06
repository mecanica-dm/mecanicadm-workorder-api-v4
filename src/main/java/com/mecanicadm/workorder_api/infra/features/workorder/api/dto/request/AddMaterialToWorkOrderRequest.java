package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request;

import jakarta.validation.constraints.Positive;

public record AddMaterialToWorkOrderRequest(
        @Positive(message = "{validation.workorder.materialitem.quantity.positive}") int quantity
) {
}
