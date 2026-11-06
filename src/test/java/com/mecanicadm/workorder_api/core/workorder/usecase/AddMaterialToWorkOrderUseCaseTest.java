package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddMaterialToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderUpdatedEvent;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
        org.mockito.Mockito.lenient().when(gateway.findDetailsById(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation ->
                java.util.Optional.of(new com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails(invocation.getArgument(0), UUID.randomUUID(), "ABC-1234", "descricao",
                        com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus.RECEIVED, null, null,
                        java.util.List.of(), java.util.List.of())));
        useCase = new AddMaterialToWorkOrderUseCase(gateway, materialItemGateway, createWorkOrderMaterialItemUseCase, publisher);
    }

    @Test
    @DisplayName("Deve criar novo material item quando não existir")
    void shouldCreateNewMaterialItemWhenNotExists() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        int quantity = 2;
        com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder workOrder = com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        workOrderId = workOrder.getId();
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(materialItemGateway.findByWorkOrderIdAndMaterialId(workOrderId, materialId)).thenReturn(Optional.empty());

        useCase.execute(new AddMaterialToWorkOrderCommand(workOrderId, materialId, quantity));

        verify(gateway, times(2)).findById(workOrderId);
        verify(materialItemGateway).findByWorkOrderIdAndMaterialId(workOrderId, materialId);
        verify(materialItemGateway).create(any(WorkOrderMaterialItem.class), eq(workOrderId));
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(org.mockito.ArgumentMatchers.any(), captor.capture());
        assertInstanceOf(WorkOrderUpdatedEvent.class, captor.getValue());
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve atualizar quantidade quando material item já existir")
    void shouldUpdateQuantityWhenMaterialItemExists() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        int quantity = 3;
        var existingItem = mock(WorkOrderMaterialItem.class);
        com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder workOrder = com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        workOrderId = workOrder.getId();
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(materialItemGateway.findByWorkOrderIdAndMaterialId(workOrderId, materialId)).thenReturn(Optional.of(existingItem));

        useCase.execute(new AddMaterialToWorkOrderCommand(workOrderId, materialId, quantity));

        verify(gateway, times(2)).findById(workOrderId);
        verify(materialItemGateway).findByWorkOrderIdAndMaterialId(workOrderId, materialId);
        verify(existingItem).addQuantity(quantity);
        verify(materialItemGateway).update(existingItem, workOrderId);
        verify(materialItemGateway, never()).create(any(), any());
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(org.mockito.ArgumentMatchers.any(), captor.capture());
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
