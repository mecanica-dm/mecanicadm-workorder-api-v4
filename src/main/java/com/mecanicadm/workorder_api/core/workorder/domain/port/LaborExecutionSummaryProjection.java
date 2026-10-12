package com.mecanicadm.workorder_api.core.workorder.domain.port;

public interface LaborExecutionSummaryProjection {
    Long getTotalProcessedLabors();
    Double getAverageExecutionMinutes();
}
