package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.usecase.query.GetAllLaborExecutionTimeReportQuery;
import com.mecanicadm.workorder_api.core.workorder.domain.port.LaborExecutionSummaryProjection;
import com.mecanicadm.workorder_api.core.workorder.domain.port.LaborTypeStatsProjection;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborExecutionReportResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa.WorkOrderLaborItemJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllLaborExecutionTimeReportUseCaseTest {

    @Mock
    private WorkOrderLaborItemJpaRepository workOrderLaborItemRepository;

    private GetAllLaborExecutionTimeReportUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetAllLaborExecutionTimeReportUseCase(workOrderLaborItemRepository);
    }

    @Test
    @DisplayName("Deve gerar relatório de execução com sucesso")
    void shouldGenerateExecutionReportSuccessfully() {
        var initialDate = LocalDate.of(2026, Month.JANUARY, 1);
        var finalDate = LocalDate.of(2026, Month.DECEMBER, 31);
        var query = new GetAllLaborExecutionTimeReportQuery(initialDate, finalDate);

        var summary = mock(LaborExecutionSummaryProjection.class);
        when(summary.getTotalProcessedLabors()).thenReturn(10L);
        when(summary.getAverageExecutionMinutes()).thenReturn(30.5);
        when(workOrderLaborItemRepository.getExecutionTimeSummary(any(), any())).thenReturn(summary);

        var statsProjection = mock(LaborTypeStatsProjection.class);
        var laborId = UUID.randomUUID();
        when(statsProjection.getLaborId()).thenReturn(laborId);
        when(statsProjection.getLaborName()).thenReturn("Troca de Óleo");
        when(statsProjection.getTotalExecutions()).thenReturn(5L);
        when(statsProjection.getAverageExecutionMinutes()).thenReturn(30.0);
        when(statsProjection.getMinExecutionMinutes()).thenReturn(15.0);
        when(statsProjection.getMaxExecutionMinutes()).thenReturn(45.0);
        when(workOrderLaborItemRepository.getStatsByLaborType(any(), any())).thenReturn(List.of(statsProjection));

        LaborExecutionReportResponse response = useCase.execute(query);

        assertNotNull(response);
        assertEquals("minutes", response.durationUnit());
        assertEquals(10L, response.totalProcessedLabors());
        assertEquals(30.5, response.averageLaborExecutionTime());
        assertEquals(1, response.statsByLaborType().size());
        assertEquals(laborId, response.statsByLaborType().getFirst().laborId());
        assertEquals("Troca de Óleo", response.statsByLaborType().getFirst().laborName());

        verify(workOrderLaborItemRepository).getExecutionTimeSummary(any(), any());
        verify(workOrderLaborItemRepository).getStatsByLaborType(any(), any());
    }

    @Test
    @DisplayName("Deve gerar relatório com valores padrão quando summary for nulo")
    void shouldGenerateReportWithDefaultsWhenSummaryIsNull() {
        var query = new GetAllLaborExecutionTimeReportQuery(null, null);

        when(workOrderLaborItemRepository.getExecutionTimeSummary(any(), any())).thenReturn(null);
        when(workOrderLaborItemRepository.getStatsByLaborType(any(), any())).thenReturn(List.of());

        LaborExecutionReportResponse response = useCase.execute(query);

        assertNotNull(response);
        assertEquals("minutes", response.durationUnit());
        assertEquals(0L, response.totalProcessedLabors());
        assertEquals(0.0, response.averageLaborExecutionTime());
        assertTrue(response.statsByLaborType().isEmpty());

        verify(workOrderLaborItemRepository).getExecutionTimeSummary(any(), any());
        verify(workOrderLaborItemRepository).getStatsByLaborType(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando período for inválido")
    void shouldThrowExceptionWhenPeriodIsInvalid() {
        var initialDate = LocalDate.of(2026, Month.DECEMBER, 31);
        var finalDate = LocalDate.of(2026, Month.JANUARY, 1);
        var query = new GetAllLaborExecutionTimeReportQuery(initialDate, finalDate);

        assertThrows(WorkOrderExceptions.InvalidReportPeriod.class, () -> useCase.execute(query));

        verify(workOrderLaborItemRepository, never()).getExecutionTimeSummary(any(), any());
        verify(workOrderLaborItemRepository, never()).getStatsByLaborType(any(), any());
    }
}
