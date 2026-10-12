package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.infra.config.jackson.ExcludeZeroFilter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WorkOrderResponse(
        UUID id,
        UUID clientId,
        String vehicleId,
        String description,
        WorkOrderStatus status,
        LocalDateTime executionStartAt,
        LocalDateTime executionEndAt,
        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = ExcludeZeroFilter.class)
        Long totalExecutionTimeInMinutes,
        List<WorkOrderLaborItemResponse> laborItems,
        List<WorkOrderMaterialItemResponse> materialItems,
        WorkOrderBudgetResponse budget,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static WorkOrderResponse from(WorkOrder workOrder) {
        return new WorkOrderResponse(
                workOrder.getId(),
                workOrder.getClientId(),
                workOrder.getVehicleId(),
                workOrder.getDescription(),
                workOrder.getStatus(),
                workOrder.getExecutionStartAt().orElse(null),
                workOrder.getExecutionEndAt().orElse(null),
                calculateTotalExecutionTime(workOrder.getExecutionStartAt().orElse(null), workOrder.getExecutionEndAt().orElse(null)),
                workOrder.getLaborItems().stream().map(WorkOrderLaborItemResponse::from).toList(),
                workOrder.getMaterialItems().stream().map(WorkOrderMaterialItemResponse::from).toList(),
                workOrder.getBudget().map(WorkOrderBudgetResponse::from).orElse(null),
                workOrder.getDateCreated(),
                workOrder.getDateUpdated()
        );
    }

    private static Long calculateTotalExecutionTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }
        return Duration.between(start, end).toMinutes();
    }
}
