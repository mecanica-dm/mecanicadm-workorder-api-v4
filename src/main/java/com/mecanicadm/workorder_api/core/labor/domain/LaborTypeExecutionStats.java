package com.mecanicadm.workorder_api.core.labor.domain;

import java.util.UUID;

public record LaborTypeExecutionStats(
        UUID laborId,
        String laborName,
        Long totalExecutions,
        Double averageExecutionTimeInMinutes,
        Double shortestExecutionTimeInMinutes,
        Double longestExecutionTimeInMinutes
) {
}
