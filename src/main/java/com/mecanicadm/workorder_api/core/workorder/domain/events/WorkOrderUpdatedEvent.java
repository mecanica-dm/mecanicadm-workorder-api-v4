package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WorkOrderUpdatedEvent(
        UUID workOrderId,
        ClientSnapshot client,
        VehicleSnapshot vehicle,
        String description,
        String status,
        LocalDateTime executionStartAt,
        LocalDateTime executionEndAt,
        List<LaborItemSnapshot> laborItems,
        List<MaterialItemSnapshot> materialItems) {

    public static WorkOrderUpdatedEvent of(WorkOrderDetails details) {
        return new WorkOrderUpdatedEvent(
                details.id(),
                details.client(),
                details.vehicle(),
                details.description(),
                details.status().name(),
                details.executionStartAt(),
                details.executionEndAt(),
                details.laborItems(),
                details.materialItems());
    }
}
