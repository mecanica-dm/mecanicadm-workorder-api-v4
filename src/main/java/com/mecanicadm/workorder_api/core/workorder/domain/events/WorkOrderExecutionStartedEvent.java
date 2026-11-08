package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;

import java.util.UUID;

public record WorkOrderExecutionStartedEvent(UUID workOrderId) {

    public static WorkOrderExecutionStartedEvent of(WorkOrder workOrder) {
        return new WorkOrderExecutionStartedEvent(workOrder.getId());
    }
}
