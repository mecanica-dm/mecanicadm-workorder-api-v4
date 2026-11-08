package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;

import java.util.UUID;

public record WorkOrderAuthorizedEvent(UUID workOrderId) {

    public static WorkOrderAuthorizedEvent of(WorkOrder workOrder) {
        return new WorkOrderAuthorizedEvent(workOrder.getId());
    }
}
