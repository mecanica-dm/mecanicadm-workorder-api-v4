package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CalculateWorkOrderBudgetCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateWorkOrderBudgetUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private CalculateWorkOrderBudgetUseCase useCase;

    @Test
    @DisplayName("Deve calcular orçamento atualizando orçamento existente")
    void shouldCalculateBudgetUpdatingExistingBudget() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        WorkOrderBudget existingBudget = mock(WorkOrderBudget.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(gateway.sumMaterialsTotalByWorkOrderId(workOrderId)).thenReturn(new BigDecimal("100.00"));
        when(gateway.sumLaborTotalByWorkOrderId(workOrderId)).thenReturn(new BigDecimal("50.00"));
        when(workOrder.getBudget()).thenReturn(Optional.of(existingBudget));
        when(workOrder.getId()).thenReturn(workOrderId);

        UUID resultId = useCase.execute(new CalculateWorkOrderBudgetCommand(workOrderId));

        assertEquals(workOrderId, resultId);
        verify(existingBudget).updateTotalPrice(new BigDecimal("150.00"));
        verify(gateway).saveBudget(existingBudget);
        verify(gateway, never()).update(any());
    }

    @Test
    @DisplayName("Deve calcular orçamento criando novo orçamento quando não existir")
    void shouldCalculateBudgetCreatingNewBudgetWhenNoneExists() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(gateway.sumMaterialsTotalByWorkOrderId(workOrderId)).thenReturn(new BigDecimal("100.00"));
        when(gateway.sumLaborTotalByWorkOrderId(workOrderId)).thenReturn(new BigDecimal("50.00"));
        when(workOrder.getBudget()).thenReturn(Optional.empty());
        when(workOrder.getId()).thenReturn(workOrderId);

        UUID resultId = useCase.execute(new CalculateWorkOrderBudgetCommand(workOrderId));

        assertEquals(workOrderId, resultId);
        verify(gateway).saveBudget(any(WorkOrderBudget.class));
        verify(gateway, never()).update(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new CalculateWorkOrderBudgetCommand(workOrderId)));

        verify(gateway).findById(workOrderId);
        verify(gateway, never()).update(any());
        verify(gateway, never()).saveBudget(any());
    }
}
