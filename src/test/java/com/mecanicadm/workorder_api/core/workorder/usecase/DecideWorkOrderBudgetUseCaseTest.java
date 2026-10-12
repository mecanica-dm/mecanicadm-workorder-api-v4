package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DecideWorkOrderBudgetCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecideWorkOrderBudgetUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private DecideWorkOrderBudgetUseCase useCase;

    private WorkOrder mockWorkOrderWithBudget(WorkOrderBudgetStatus budgetStatus) {
        WorkOrder workOrder = mock(WorkOrder.class);
        WorkOrderBudget budget = mock(WorkOrderBudget.class);
        when(budget.getStatus()).thenReturn(budgetStatus);
        when(workOrder.getBudget()).thenReturn(Optional.of(budget));
        return workOrder;
    }

    @Test
    @DisplayName("Deve aprovar o orçamento")
    void shouldApproveBudget() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mockWorkOrderWithBudget(WorkOrderBudgetStatus.WAITING_DECISION);
        WorkOrderBudget budget = workOrder.getBudget().orElseThrow();
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        useCase.execute(new DecideWorkOrderBudgetCommand(workOrderId, WorkOrderBudgetStatus.APPROVED, null));

        verify(budget).approve(null);
        verify(gateway).saveBudget(budget);
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve rejeitar o orçamento")
    void shouldRejectBudget() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mockWorkOrderWithBudget(WorkOrderBudgetStatus.WAITING_DECISION);
        WorkOrderBudget budget = workOrder.getBudget().orElseThrow();
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        useCase.execute(new DecideWorkOrderBudgetCommand(workOrderId, WorkOrderBudgetStatus.REJECTED, "Motivo"));

        verify(budget).reject("Motivo", false);
        verify(gateway).saveBudget(budget);
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve solicitar alterações no orçamento")
    void shouldRequestChangesOnBudget() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mockWorkOrderWithBudget(WorkOrderBudgetStatus.WAITING_DECISION);
        WorkOrderBudget budget = workOrder.getBudget().orElseThrow();
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        useCase.execute(new DecideWorkOrderBudgetCommand(workOrderId, WorkOrderBudgetStatus.CHANGES_REQUESTED, "Alterar"));

        verify(budget).reject("Alterar", true);
        verify(gateway).saveBudget(budget);
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o orçamento não está aguardando decisão")
    void shouldThrowExceptionWhenBudgetNotWaitingDecision() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mockWorkOrderWithBudget(WorkOrderBudgetStatus.APPROVED);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        assertThrows(WorkOrderExceptions.BudgetNotWaitingDecision.class,
                () -> useCase.execute(new DecideWorkOrderBudgetCommand(workOrderId, WorkOrderBudgetStatus.APPROVED, null)));

        verify(gateway, never()).create(any());
        verify(gateway, never()).saveBudget(any());
        verify(gateway, never()).update(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando motivo de rejeição não for informado")
    void shouldThrowExceptionWhenRejectionReasonIsRequired() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mockWorkOrderWithBudget(WorkOrderBudgetStatus.WAITING_DECISION);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        assertThrows(WorkOrderExceptions.BudgetObservationRequired.class,
                () -> useCase.execute(new DecideWorkOrderBudgetCommand(workOrderId, WorkOrderBudgetStatus.REJECTED, "")));

        verify(gateway, never()).create(any());
        verify(gateway, never()).saveBudget(any());
        verify(gateway, never()).update(any());
    }
}
