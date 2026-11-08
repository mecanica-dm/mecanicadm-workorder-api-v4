package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddMaterialToWorkOrderCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddMaterialToWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @Mock
    private WorkOrderMaterialItemGateway materialItemGateway;

    @Mock
    private CreateWorkOrderMaterialItemUseCase createWorkOrderMaterialItemUseCase;

    @Mock
    private MessagePublisher publisher;

    private AddMaterialToWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(gateway.findDetailsById(ArgumentMatchers.any())).thenAnswer(invocation ->
                Optional.of(new WorkOrderDetails(invocation.getArgument(0), null, null, "descricao",
                        WorkOrderStatus.RECEIVED, null, null,
                        List.of(), List.of())));
        useCase = new AddMaterialToWorkOrderUseCase(gateway, materialItemGateway, createWorkOrderMaterialItemUseCase, publisher);
    }

    @Test
    @DisplayName("Deve criar novo material item quando não existir")
    void shouldCreateNewMaterialItemWhenNotExists() {
        UUID materialId = UUID.randomUUID();
        int quantity = 2;
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        UUID workOrderId = workOrder.getId();
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(materialItemGateway.findByWorkOrderIdAndMaterialId(workOrderId, materialId)).thenReturn(Optional.empty());

        useCase.execute(new AddMaterialToWorkOrderCommand(workOrderId, materialId, quantity));

        verify(gateway, times(2)).findById(workOrderId);
        verify(materialItemGateway).findByWorkOrderIdAndMaterialId(workOrderId, materialId);
        verify(materialItemGateway).create(any(WorkOrderMaterialItem.class), eq(workOrderId));
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(ArgumentMatchers.any(), captor.capture());
        assertInstanceOf(WorkOrderUpdatedEvent.class, captor.getValue());
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve atualizar quantidade quando material item já existir")
    void shouldUpdateQuantityWhenMaterialItemExists() {
        UUID materialId = UUID.randomUUID();
        int quantity = 3;
        var existingItem = mock(WorkOrderMaterialItem.class);
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        UUID workOrderId = workOrder.getId();
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(materialItemGateway.findByWorkOrderIdAndMaterialId(workOrderId, materialId)).thenReturn(Optional.of(existingItem));

        useCase.execute(new AddMaterialToWorkOrderCommand(workOrderId, materialId, quantity));

        verify(gateway, times(2)).findById(workOrderId);
        verify(materialItemGateway).findByWorkOrderIdAndMaterialId(workOrderId, materialId);
        verify(existingItem).addQuantity(quantity);
        verify(materialItemGateway).update(existingItem, workOrderId);
        verify(materialItemGateway, never()).create(any(), any());
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(ArgumentMatchers.any(), captor.capture());
        assertInstanceOf(WorkOrderUpdatedEvent.class, captor.getValue());
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        int quantity = 1;
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new AddMaterialToWorkOrderCommand(workOrderId, materialId, quantity)));

        verify(gateway).findById(workOrderId);
        verifyNoInteractions(materialItemGateway);
    }
}
