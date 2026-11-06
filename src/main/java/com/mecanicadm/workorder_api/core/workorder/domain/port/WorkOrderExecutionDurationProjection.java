package com.mecanicadm.workorder_api.core.workorder.domain.port;

public interface WorkOrderExecutionDurationProjection {
    Object getWorkOrderId();
    Double getDurationMinutes();
}
