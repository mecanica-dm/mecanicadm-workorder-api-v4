package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.usecase.query.GetAllLaborExecutionTimeReportQuery;
import com.mecanicadm.workorder_api.core.workorder.domain.port.LaborExecutionSummaryProjection;
import com.mecanicadm.workorder_api.core.workorder.domain.port.LaborTypeStatsProjection;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborExecutionReportResponse;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborTypeExecutionStatsResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa.WorkOrderLaborItemJpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GetAllLaborExecutionTimeReportUseCase {

    private static final String DURATION_UNIT = "minutes";

    private final WorkOrderLaborItemJpaRepository workOrderLaborItemRepository;

    public GetAllLaborExecutionTimeReportUseCase(WorkOrderLaborItemJpaRepository workOrderLaborItemRepository) {
        this.workOrderLaborItemRepository = workOrderLaborItemRepository;
    }

    public LaborExecutionReportResponse execute(GetAllLaborExecutionTimeReportQuery query) {
        validatePeriod(query.initialDate(), query.finalDate());

        LocalDateTime initialDateTime = toInitialDateTime(query.initialDate());
        LocalDateTime finalDateTimeExclusive = toFinalDateTimeExclusive(query.finalDate());

        LaborExecutionSummaryProjection summary =
                workOrderLaborItemRepository.getExecutionTimeSummary(initialDateTime, finalDateTimeExclusive);

        List<LaborTypeExecutionStatsResponse> statsByLaborType = workOrderLaborItemRepository
                .getStatsByLaborType(initialDateTime, finalDateTimeExclusive)
                .stream()
                .map(this::toLaborTypeStatsResponse)
                .toList();

        long total = summary != null && summary.getTotalProcessedLabors() != null ? summary.getTotalProcessedLabors() : 0L;
        double average = summary != null && summary.getAverageExecutionMinutes() != null ? summary.getAverageExecutionMinutes() : 0D;

        return new LaborExecutionReportResponse(DURATION_UNIT, total, average, statsByLaborType);
    }

    private void validatePeriod(LocalDate initialDate, LocalDate finalDate) {
        if (initialDate != null && finalDate != null && initialDate.isAfter(finalDate)) {
            throw new WorkOrderExceptions.InvalidReportPeriod();
        }
    }

    private LocalDateTime toInitialDateTime(LocalDate initialDate) {
        return initialDate != null ? initialDate.atStartOfDay() : null;
    }

    private LocalDateTime toFinalDateTimeExclusive(LocalDate finalDate) {
        return finalDate != null ? finalDate.plusDays(1).atStartOfDay() : null;
    }

    private LaborTypeExecutionStatsResponse toLaborTypeStatsResponse(LaborTypeStatsProjection projection) {
        UUID laborId = UUID.fromString(projection.getLaborId().toString());
        return new LaborTypeExecutionStatsResponse(
                laborId,
                projection.getLaborName(),
                projection.getTotalExecutions(),
                projection.getAverageExecutionMinutes(),
                projection.getMinExecutionMinutes(),
                projection.getMaxExecutionMinutes()
        );
    }
}
