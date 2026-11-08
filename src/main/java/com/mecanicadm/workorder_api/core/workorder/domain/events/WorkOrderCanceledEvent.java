package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;

import java.util.UUID;

public record WorkOrderCanceledEvent(UUID workOrderId) {

    public static WorkOrderCanceledEvent of(WorkOrder workOrder) {
        return new WorkOrderCanceledEvent(workOrder.getId());
    }
}
