package com.mecanicadm.workorder_api.core.workorder.domain.port;

import java.util.UUID;

public interface LaborTypeStatsProjection {
    UUID getLaborId();
    String getLaborName();
    Long getTotalExecutions();
    Double getAverageExecutionMinutes();
    Double getMinExecutionMinutes();
    Double getMaxExecutionMinutes();
}
