package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;

import java.util.UUID;

public record WorkOrderChangesRequestedEvent(UUID workOrderId, String observation) {

    public static WorkOrderChangesRequestedEvent of(WorkOrder workOrder, String observation) {
        return new WorkOrderChangesRequestedEvent(workOrder.getId(), observation);
    }
}
