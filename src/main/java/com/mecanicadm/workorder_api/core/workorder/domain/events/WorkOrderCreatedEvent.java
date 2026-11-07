package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WorkOrderCreatedEvent(
        UUID workOrderId,
        ClientSnapshot client,
        VehicleSnapshot vehicle,
        String description,
        String status,
        LocalDateTime executionStartAt,
        LocalDateTime executionEndAt,
        List<LaborItemSnapshot> laborItems,
        List<MaterialItemSnapshot> materialItems) {

    public static WorkOrderCreatedEvent of(WorkOrderDetails details) {
        return new WorkOrderCreatedEvent(
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
