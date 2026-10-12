package com.mecanicadm.workorder_api.core.labor.domain;

import java.util.List;

public record LaborExecutionReport(
        String durationUnit,
        Long totalProcessedLabors,
        Double averageLaborExecutionTime,
        List<LaborTypeExecutionStats> statsByLaborType
) {
}
