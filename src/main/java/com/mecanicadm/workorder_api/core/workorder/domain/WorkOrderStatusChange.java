package com.mecanicadm.workorder_api.core.workorder.domain;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;

import java.time.LocalDateTime;

public record WorkOrderStatusChange(
        WorkOrderStatus fromStatus,
        WorkOrderStatus toStatus,
        String reason,
        String changedBy,
        LocalDateTime changedAt) {

    public WorkOrderStatusChange withChangedBy(String actor) {
        return new WorkOrderStatusChange(fromStatus, toStatus, reason, actor, changedAt);
    }
}
