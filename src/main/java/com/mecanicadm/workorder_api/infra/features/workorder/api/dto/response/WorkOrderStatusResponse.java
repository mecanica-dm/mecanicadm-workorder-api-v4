package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;

import java.util.UUID;

public record WorkOrderStatusResponse(
        UUID id,
        WorkOrderStatus status
) {
    public static WorkOrderStatusResponse from(UUID id, WorkOrderStatus status) {
        return new WorkOrderStatusResponse(id, status);
    }
}
