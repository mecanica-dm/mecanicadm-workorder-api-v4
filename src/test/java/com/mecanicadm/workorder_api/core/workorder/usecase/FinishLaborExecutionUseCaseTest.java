package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishLaborExecutionCommand;
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
class FinishLaborExecutionUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @Mock
    private WorkOrderLaborItemGateway laborItemGateway;

    @InjectMocks
    private FinishLaborExecutionUseCase useCase;

    @Test
    @DisplayName("Deve finalizar a execução de um serviço com sucesso")
    void shouldFinishLaborExecutionSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        WorkOrderLaborItem laborItem = mock(WorkOrderLaborItem.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(workOrder.findLaborItem(laborItemId)).thenReturn(Optional.of(laborItem));

        useCase.execute(new FinishLaborExecutionCommand(workOrderId, laborItemId));

        verify(laborItem).finishExecution();
        verify(laborItemGateway).update(laborItem);
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new FinishLaborExecutionCommand(workOrderId, laborItemId)));

        verify(gateway, never()).update(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o item de serviço não for encontrado")
    void shouldThrowExceptionWhenLaborItemNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(workOrder.findLaborItem(laborItemId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.LaborItemNotFound.class,
                () -> useCase.execute(new FinishLaborExecutionCommand(workOrderId, laborItemId)));

        verify(gateway, never()).update(any());
        verify(laborItemGateway, never()).update(any());
    }
}
