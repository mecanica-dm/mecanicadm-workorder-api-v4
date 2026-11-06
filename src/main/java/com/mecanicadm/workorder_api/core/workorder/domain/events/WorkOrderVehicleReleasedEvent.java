package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;

import java.util.UUID;

public record WorkOrderVehicleReleasedEvent(UUID workOrderId) {

    public static WorkOrderVehicleReleasedEvent of(WorkOrder workOrder) {
        return new WorkOrderVehicleReleasedEvent(workOrder.getId());
    }
}
