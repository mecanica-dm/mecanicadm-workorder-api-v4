package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.usecase.*;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableBudgetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderBudgetControllerUnitTest {

    @Mock
    private SendWorkOrderBudgetUseCase sendWorkOrderBudgetUseCase;
    @Mock
    private ManuallyAdjustWorkOrderBudgetUseCase manuallyAdjustWorkOrderBudgetUseCase;
    @Mock
    private DecideWorkOrderBudgetUseCase decideWorkOrderBudgetUseCase;
    @Mock
    private CalculateWorkOrderBudgetUseCase calculateWorkOrderBudgetUseCase;
    @Mock
    private GetPrintableBudgetUseCase getPrintableBudgetUseCase;

    @InjectMocks
    private WorkOrderBudgetController controller;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "baseUrl", "http://localhost:/api");
    }

    @Test
    @DisplayName("Deve enviar orcamento e retornar 204")
    void shouldSendBudgetAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        var response = controller.sendBudget(workOrderId);

        assertEquals(204, response.getStatusCode().value());
        verify(sendWorkOrderBudgetUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve ajustar orcamento e retornar 204")
    void shouldAdjustBudgetAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        var request = new com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.ManuallyAdjustWorkOrderBudgetRequest(java.math.BigDecimal.valueOf(500));

        var response = controller.adjustBudget(workOrderId, request);

        assertEquals(204, response.getStatusCode().value());
        verify(manuallyAdjustWorkOrderBudgetUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve recalcular orcamento e retornar 204")
    void shouldRecalculateBudgetAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        var response = controller.recalculateBudget(workOrderId);

        assertEquals(204, response.getStatusCode().value());
        verify(calculateWorkOrderBudgetUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve decidir orcamento e retornar 204")
    void shouldDecideBudgetAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        var request = new com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.DecideWorkOrderBudgetRequest(
                com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus.APPROVED, null);

        var response = controller.decideBudget(workOrderId, request);

        assertEquals(204, response.getStatusCode().value());
        verify(decideWorkOrderBudgetUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve imprimir orcamento e retornar 200")
    void shouldPrintBudgetAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        PrintableBudgetResponse pdfResponse = new PrintableBudgetResponse("budget.pdf", "base64content");
        when(getPrintableBudgetUseCase.execute(any())).thenReturn(pdfResponse);

        var response = controller.printBudget(workOrderId);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("budget.pdf", response.getBody().fileName());
    }
}
