package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.UpdateWorkOrderCommand;
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
class UpdateWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private UpdateWorkOrderUseCase useCase;

    @Test
    @DisplayName("Deve atualizar uma ordem de serviço existente com sucesso")
    void shouldUpdateExistingWorkOrderSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        UpdateWorkOrderCommand command = new UpdateWorkOrderCommand(
                workOrderId, "ABC-1234", UUID.randomUUID(), "Descricao atualizada");

        useCase.execute(command);

        verify(gateway).findById(workOrderId);
        verify(workOrder).update(command.clientId(), command.vehicleId(), command.description());
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada para atualização")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        UpdateWorkOrderCommand command = new UpdateWorkOrderCommand(
                workOrderId, "ABC-1234", UUID.randomUUID(), "Descricao");

        assertThrows(WorkOrderExceptions.NotFound.class, () -> useCase.execute(command));

        verify(gateway).findById(workOrderId);
        verify(gateway, never()).update(any());
    }
}
