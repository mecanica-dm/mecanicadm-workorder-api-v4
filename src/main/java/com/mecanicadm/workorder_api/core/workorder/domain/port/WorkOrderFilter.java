package com.mecanicadm.workorder_api.core.workorder.domain.port;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;

import java.util.Set;
import java.util.UUID;

public record WorkOrderFilter(
        UUID clientId,
        String licensePlate,
        Set<WorkOrderStatus> statuses
) {
}
