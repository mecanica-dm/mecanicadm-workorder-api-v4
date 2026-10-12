package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

public record WorkOrderExecutionReportResponse(
        String durationUnit,
        Long totalProcessedWorkOrders,
        Double averageWorkOrderExecutionTime,
        WorkOrderExecutionTimeInfoResponse slowestWorkOrder,
        WorkOrderExecutionTimeInfoResponse fastestWorkOrder,
        Double averageWorkOrderLaborExecutionTime
) {
}

