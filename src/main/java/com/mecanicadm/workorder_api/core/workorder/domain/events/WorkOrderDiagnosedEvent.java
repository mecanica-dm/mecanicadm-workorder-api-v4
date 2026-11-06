package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;

import java.util.UUID;

public record WorkOrderDiagnosedEvent(UUID workOrderId) {

    public static WorkOrderDiagnosedEvent of(WorkOrder workOrder) {
        return new WorkOrderDiagnosedEvent(workOrder.getId());
    }
}
