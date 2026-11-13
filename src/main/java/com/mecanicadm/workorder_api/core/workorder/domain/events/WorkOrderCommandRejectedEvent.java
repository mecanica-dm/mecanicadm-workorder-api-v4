package com.mecanicadm.workorder_api.core.workorder.domain.events;

import java.util.UUID;

public record WorkOrderCommandRejectedEvent(UUID workOrderId, String eventType, String exception, String message) {
}
