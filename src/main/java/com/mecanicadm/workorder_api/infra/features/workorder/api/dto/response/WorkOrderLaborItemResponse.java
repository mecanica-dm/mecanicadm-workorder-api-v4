package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.LaborExecutionStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkOrderLaborItemResponse(
        UUID id,
        UUID laborId,
        LaborExecutionStatus status,
        LocalDateTime executionStartAt,
        LocalDateTime executionEndAt,
        Long totalExecutionTimeInMinutes
) {
    public static WorkOrderLaborItemResponse from(WorkOrderLaborItem laborItem) {
        return new WorkOrderLaborItemResponse(
                laborItem.getId(),
                laborItem.getLaborId(),
                laborItem.getStatus(),
                laborItem.getExecutionStartAt(),
                laborItem.getExecutionEndAt(),
                calculateTotalExecutionTime(laborItem.getExecutionStartAt(), laborItem.getExecutionEndAt())
        );
    }

    private static Long calculateTotalExecutionTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }
        return Duration.between(start, end).toMinutes();
    }
}
