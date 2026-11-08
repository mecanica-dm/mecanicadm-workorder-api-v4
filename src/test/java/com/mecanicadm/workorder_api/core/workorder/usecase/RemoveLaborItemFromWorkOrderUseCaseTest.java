package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveLaborItemFromWorkOrderCommand;
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
class RemoveLaborItemFromWorkOrderUseCaseTest {

    @Mock
    private WorkOrderLaborItemGateway gateway;

    @Mock
    private WorkOrderGateway workOrderGateway;

    @Mock
    private MessagePublisher publisher;

    private RemoveLaborItemFromWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(workOrderGateway.findDetailsById(ArgumentMatchers.any())).thenAnswer(invocation ->
                Optional.of(new WorkOrderDetails(invocation.getArgument(0), null, null, "descricao",
                        WorkOrderStatus.RECEIVED, null, null,
                        List.of(), List.of())));
        useCase = new RemoveLaborItemFromWorkOrderUseCase(gateway, workOrderGateway, publisher);
    }

    @Test
    @DisplayName("Deve remover o item de serviço com sucesso")
    void shouldRemoveLaborItemSuccessfully() {
        UUID laborItemId = UUID.randomUUID();
        WorkOrderLaborItem laborItem = mock(WorkOrderLaborItem.class);
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        UUID workOrderId = workOrder.getId();
        when(gateway.findById(laborItemId)).thenReturn(Optional.of(laborItem));
        when(laborItem.getWorkOrderId()).thenReturn(workOrderId);
        when(workOrderGateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        useCase.execute(new RemoveLaborItemFromWorkOrderCommand(workOrderId, laborItemId));

        verify(gateway).delete(laborItem);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(ArgumentMatchers.any(), captor.capture());
        assertInstanceOf(WorkOrderUpdatedEvent.class, captor.getValue());
        verify(workOrderGateway).update(workOrder);
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
