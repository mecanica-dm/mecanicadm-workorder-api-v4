package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.ManuallyAdjustWorkOrderBudgetCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManuallyAdjustWorkOrderBudgetUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private ManuallyAdjustWorkOrderBudgetUseCase useCase;

    @Test
    @DisplayName("Deve ajustar o orçamento manualmente com sucesso")
    void shouldAdjustBudgetSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        BigDecimal newTotalPrice = new BigDecimal("350.00");
        WorkOrder workOrder = mock(WorkOrder.class);
        WorkOrderBudget budget = mock(WorkOrderBudget.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(workOrder.getBudget()).thenReturn(Optional.of(budget));

        useCase.execute(new ManuallyAdjustWorkOrderBudgetCommand(workOrderId, newTotalPrice));

        verify(budget).updateTotalPrice(newTotalPrice);
        verify(gateway).saveBudget(budget);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new ManuallyAdjustWorkOrderBudgetCommand(workOrderId, BigDecimal.TEN)));

        verify(gateway, never()).update(any());
        verify(gateway, never()).saveBudget(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o orçamento não for encontrado")
    void shouldThrowExceptionWhenBudgetNotFound() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(workOrder.getBudget()).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.BudgetNotFound.class,
                () -> useCase.execute(new ManuallyAdjustWorkOrderBudgetCommand(workOrderId, BigDecimal.TEN)));

        verify(gateway, never()).update(any());
        verify(gateway, never()).saveBudget(any());
    }
}
