package com.mecanicadm.workorder_api.infra.features.labor.api.dto.response;

import com.mecanicadm.workorder_api.core.labor.domain.LaborTypeExecutionStats;

import java.util.UUID;

public record LaborTypeExecutionStatsResponse(
        UUID laborId,
        String laborName,
        Long totalExecutions,
        Double averageExecutionTimeInMinutes,
        Double shortestExecutionTimeInMinutes,
        Double longestExecutionTimeInMinutes
) {
    public static LaborTypeExecutionStatsResponse from(LaborTypeExecutionStats stats) {
        return new LaborTypeExecutionStatsResponse(
                stats.laborId(),
                stats.laborName(),
                stats.totalExecutions(),
                stats.averageExecutionTimeInMinutes(),
                stats.shortestExecutionTimeInMinutes(),
                stats.longestExecutionTimeInMinutes()
        );
    }
}

