package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderExecutionDurationProjection;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderExecutionSummaryProjection;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetAllWorkOrderExecutionTimeReportQuery;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderExecutionReportResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllWorkOrderExecutionTimeReportUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private GetAllWorkOrderExecutionTimeReportUseCase useCase;

    @Test
    @DisplayName("Deve gerar relatório com dados completos")
    void shouldGenerateReportWithFullData() {
        LocalDate initialDate = LocalDate.of(2024, Month.JANUARY, 1);
        LocalDate finalDate = LocalDate.of(2024, Month.DECEMBER, 31);
        GetAllWorkOrderExecutionTimeReportQuery query =
                new GetAllWorkOrderExecutionTimeReportQuery(initialDate, finalDate);

        WorkOrderExecutionSummaryProjection summary = mock(WorkOrderExecutionSummaryProjection.class);
        when(summary.getTotalProcessedWorkOrders()).thenReturn(10L);
        when(summary.getAverageExecutionMinutes()).thenReturn(120.0);

        WorkOrderExecutionDurationProjection slowest = mock(WorkOrderExecutionDurationProjection.class);
        UUID slowestId = UUID.randomUUID();
        when(slowest.getWorkOrderId()).thenReturn(slowestId);
        when(slowest.getDurationMinutes()).thenReturn(300.0);

        WorkOrderExecutionDurationProjection fastest = mock(WorkOrderExecutionDurationProjection.class);
        UUID fastestId = UUID.randomUUID();
        when(fastest.getWorkOrderId()).thenReturn(fastestId);
        when(fastest.getDurationMinutes()).thenReturn(30.0);

        when(gateway.getExecutionTimeSummary(any(), any())).thenReturn(summary);
        when(gateway.getSlowestExecution(any(), any())).thenReturn(slowest);
        when(gateway.getFastestExecution(any(), any())).thenReturn(fastest);
        when(gateway.getAverageLaborExecutionMinutes(any(), any())).thenReturn(60.0);

        WorkOrderExecutionReportResponse response = useCase.execute(query);

        assertNotNull(response);
        assertEquals("minutes", response.durationUnit());
        assertEquals(10L, response.totalProcessedWorkOrders());
        assertEquals(120.0, response.averageWorkOrderExecutionTime());
        assertNotNull(response.slowestWorkOrder());
        assertEquals(slowestId, response.slowestWorkOrder().workOrderId());
        assertEquals(300.0, response.slowestWorkOrder().durationInMinutes());
        assertNotNull(response.fastestWorkOrder());
        assertEquals(fastestId, response.fastestWorkOrder().workOrderId());
        assertEquals(30.0, response.fastestWorkOrder().durationInMinutes());
        assertEquals(60.0, response.averageWorkOrderLaborExecutionTime());
    }

    @Test
    @DisplayName("Deve gerar relatório com projeções nulas")
    void shouldGenerateReportWithNullProjections() {
        LocalDate initialDate = LocalDate.of(2024, Month.JANUARY, 1);
        LocalDate finalDate = LocalDate.of(2024, Month.DECEMBER, 31);
        GetAllWorkOrderExecutionTimeReportQuery query =
                new GetAllWorkOrderExecutionTimeReportQuery(initialDate, finalDate);

        when(gateway.getExecutionTimeSummary(any(), any())).thenReturn(null);
        when(gateway.getSlowestExecution(any(), any())).thenReturn(null);
        when(gateway.getFastestExecution(any(), any())).thenReturn(null);
        when(gateway.getAverageLaborExecutionMinutes(any(), any())).thenReturn(null);

        WorkOrderExecutionReportResponse response = useCase.execute(query);

        assertNotNull(response);
        assertEquals(0L, response.totalProcessedWorkOrders());
        assertEquals(0D, response.averageWorkOrderExecutionTime());
        assertNull(response.slowestWorkOrder());
        assertNull(response.fastestWorkOrder());
        assertEquals(0D, response.averageWorkOrderLaborExecutionTime());
    }

    @Test
    @DisplayName("Deve lançar exceção quando período for inválido")
    void shouldThrowExceptionWhenPeriodIsInvalid() {
        LocalDate initialDate = LocalDate.of(2024, Month.DECEMBER, 31);
        LocalDate finalDate = LocalDate.of(2024, Month.JANUARY, 1);
        GetAllWorkOrderExecutionTimeReportQuery query =
                new GetAllWorkOrderExecutionTimeReportQuery(initialDate, finalDate);

        assertThrows(WorkOrderExceptions.InvalidReportPeriod.class,
                () -> useCase.execute(query));

        verifyNoInteractions(gateway);
    }
}
