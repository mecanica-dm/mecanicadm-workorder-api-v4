package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;

import java.util.UUID;

public record WorkOrderVehicleDeliveredEvent(UUID workOrderId) {

    public static WorkOrderVehicleDeliveredEvent of(WorkOrder workOrder) {
        return new WorkOrderVehicleDeliveredEvent(workOrder.getId());
    }
}
