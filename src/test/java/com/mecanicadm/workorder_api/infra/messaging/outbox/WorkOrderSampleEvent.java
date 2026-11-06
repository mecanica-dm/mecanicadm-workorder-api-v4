package com.mecanicadm.workorder_api.infra.messaging.outbox;

import java.util.UUID;

record WorkOrderSampleEvent(UUID workOrderId, String note) {
    static WorkOrderSampleEvent of(String note) {
        return new WorkOrderSampleEvent(UUID.randomUUID(), note);
    }
}
