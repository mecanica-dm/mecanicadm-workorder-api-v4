package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartLaborExecutionCommand;
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
class StartLaborExecutionUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @Mock
    private WorkOrderLaborItemGateway laborItemGateway;

    @InjectMocks
    private StartLaborExecutionUseCase useCase;

    @Test
    @DisplayName("Deve iniciar a execução de um serviço com sucesso")
    void shouldStartLaborExecutionSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        WorkOrderLaborItem laborItem = mock(WorkOrderLaborItem.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(workOrder.findLaborItem(laborItemId)).thenReturn(Optional.of(laborItem));

        useCase.execute(new StartLaborExecutionCommand(workOrderId, laborItemId));

        verify(workOrder).startLaborItem(laborItemId);
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
                () -> useCase.execute(new StartLaborExecutionCommand(workOrderId, laborItemId)));

        verify(gateway, never()).update(any());
    }
}
