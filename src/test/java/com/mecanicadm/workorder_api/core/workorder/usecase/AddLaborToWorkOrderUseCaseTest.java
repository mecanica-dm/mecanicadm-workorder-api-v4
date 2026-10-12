package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddLaborToWorkOrderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddLaborToWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @Mock
    private WorkOrderLaborItemGateway laborItemGateway;

    @Mock
    private CreateWorkOrderLaborItemUseCase createWorkOrderLaborItemUseCase;

    private AddLaborToWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddLaborToWorkOrderUseCase(gateway, laborItemGateway, createWorkOrderLaborItemUseCase);
    }

    @Test
    @DisplayName("Deve adicionar serviço à ordem de serviço com sucesso")
    void shouldAddLaborToWorkOrderSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();
        WorkOrderLaborItem laborItem = mock(WorkOrderLaborItem.class);
        when(gateway.existsById(workOrderId)).thenReturn(true);
        when(createWorkOrderLaborItemUseCase.execute(any())).thenReturn(laborItem);

        useCase.execute(new AddLaborToWorkOrderCommand(workOrderId, laborId));

        verify(gateway).existsById(workOrderId);
        verify(laborItemGateway).create(laborItem);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();
        when(gateway.existsById(workOrderId)).thenReturn(false);

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new AddLaborToWorkOrderCommand(workOrderId, laborId)));

        verify(gateway).existsById(workOrderId);
        verifyNoInteractions(laborItemGateway);
    }
}
