package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddLaborToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderUpdatedEvent;
import java.util.List;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    @Mock
    private MessagePublisher publisher;

    private AddLaborToWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(gateway.findDetailsById(ArgumentMatchers.any())).thenAnswer(invocation ->
                Optional.of(new WorkOrderDetails(invocation.getArgument(0), null, null, "descricao",
                        WorkOrderStatus.RECEIVED, null, null,
                        List.of(), List.of())));
        useCase = new AddLaborToWorkOrderUseCase(gateway, laborItemGateway, createWorkOrderLaborItemUseCase, publisher);
    }

    @Test
    @DisplayName("Deve adicionar serviço à ordem de serviço com sucesso")
    void shouldAddLaborToWorkOrderSuccessfully() {
        UUID laborId = UUID.randomUUID();
        WorkOrderLaborItem laborItem = mock(WorkOrderLaborItem.class);
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        UUID workOrderId = workOrder.getId();
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(createWorkOrderLaborItemUseCase.execute(any())).thenReturn(laborItem);

        useCase.execute(new AddLaborToWorkOrderCommand(workOrderId, laborId));

        verify(gateway, times(2)).findById(workOrderId);
        verify(laborItemGateway).create(laborItem);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(ArgumentMatchers.any(), captor.capture());
        assertInstanceOf(WorkOrderUpdatedEvent.class, captor.getValue());
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new AddLaborToWorkOrderCommand(workOrderId, laborId)));

        verify(gateway).findById(workOrderId);
        verifyNoInteractions(laborItemGateway);
    }
}
