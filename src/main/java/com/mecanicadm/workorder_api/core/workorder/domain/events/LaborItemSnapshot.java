package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.LaborExecutionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LaborItemSnapshot(
        UUID id,
        UUID laborId,
        String name,
        BigDecimal price,
        LaborExecutionStatus status,
        LocalDateTime executionStartAt,
        LocalDateTime executionEndAt) {
}
