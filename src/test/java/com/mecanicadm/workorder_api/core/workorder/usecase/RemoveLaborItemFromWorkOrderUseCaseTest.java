package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveLaborItemFromWorkOrderCommand;
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
class RemoveLaborItemFromWorkOrderUseCaseTest {

    @Mock
    private WorkOrderLaborItemGateway gateway;

    @InjectMocks
    private RemoveLaborItemFromWorkOrderUseCase useCase;

    @Test
    @DisplayName("Deve remover o item de serviço com sucesso")
    void shouldRemoveLaborItemSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        WorkOrderLaborItem laborItem = mock(WorkOrderLaborItem.class);
        when(gateway.findById(laborItemId)).thenReturn(Optional.of(laborItem));

        useCase.execute(new RemoveLaborItemFromWorkOrderCommand(workOrderId, laborItemId));

        verify(gateway).delete(laborItem);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o item de serviço não for encontrado")
    void shouldThrowExceptionWhenLaborItemNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        when(gateway.findById(laborItemId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.LaborItemNotFound.class,
                () -> useCase.execute(new RemoveLaborItemFromWorkOrderCommand(workOrderId, laborItemId)));

        verify(gateway, never()).delete(any());
    }
}
