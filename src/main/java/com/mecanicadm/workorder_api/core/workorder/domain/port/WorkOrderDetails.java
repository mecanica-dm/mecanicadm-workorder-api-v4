package com.mecanicadm.workorder_api.core.workorder.domain.port;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.events.LaborItemSnapshot;
import com.mecanicadm.workorder_api.core.workorder.domain.events.MaterialItemSnapshot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WorkOrderDetails(
        UUID id,
        UUID clientId,
        String vehicleId,
        String description,
        WorkOrderStatus status,
        LocalDateTime executionStartAt,
        LocalDateTime executionEndAt,
        List<LaborItemSnapshot> laborItems,
        List<MaterialItemSnapshot> materialItems) {
}
