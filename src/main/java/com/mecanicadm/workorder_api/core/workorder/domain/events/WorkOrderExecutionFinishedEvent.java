package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;

import java.util.UUID;

public record WorkOrderExecutionFinishedEvent(UUID workOrderId) {

    public static WorkOrderExecutionFinishedEvent of(WorkOrder workOrder) {
        return new WorkOrderExecutionFinishedEvent(workOrder.getId());
    }
}
