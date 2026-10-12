package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import java.util.UUID;

public record WorkOrderExecutionTimeInfoResponse(
        UUID workOrderId,
        Double durationInMinutes
) {
}

