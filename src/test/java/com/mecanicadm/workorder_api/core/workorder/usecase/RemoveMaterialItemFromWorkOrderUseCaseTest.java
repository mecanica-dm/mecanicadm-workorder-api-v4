package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.usecase.SoftDeleteStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.SoftDeleteStockCommand;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveMaterialItemFromWorkOrderCommand;
import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderUpdatedEvent;
import org.mockito.ArgumentCaptor;
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
class RemoveMaterialItemFromWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway workOrderGateway;

    @Mock
    private WorkOrderMaterialItemGateway materialItemGateway;

    @Mock
    private SoftDeleteStockUseCase softDeleteStockUseCase;

    @Mock
    private MessagePublisher publisher;

    private RemoveMaterialItemFromWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient().when(workOrderGateway.findDetailsById(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation ->
                java.util.Optional.of(new com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails(invocation.getArgument(0), UUID.randomUUID(), "ABC-1234", "descricao",
                        com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus.RECEIVED, null, null,
                        java.util.List.of(), java.util.List.of())));
        useCase = new RemoveMaterialItemFromWorkOrderUseCase(workOrderGateway, materialItemGateway, softDeleteStockUseCase, publisher);
    }

    @Test
    @DisplayName("Deve remover o item de material com sucesso")
    void shouldRemoveMaterialItemSuccessfully() {
        UUID materialId = UUID.randomUUID();
        com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder workOrder = com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        UUID workOrderId = workOrder.getId();
        when(workOrderGateway.findById(workOrderId)).thenReturn(java.util.Optional.of(workOrder));

        useCase.execute(new RemoveMaterialItemFromWorkOrderCommand(workOrderId, materialId));

        verify(workOrderGateway, times(2)).findById(workOrderId);
        verify(softDeleteStockUseCase).execute(new SoftDeleteStockCommand(materialId, workOrderId));
        verify(materialItemGateway).deleteByWorkOrderIdAndMaterialId(workOrderId, materialId);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(org.mockito.ArgumentMatchers.any(), captor.capture());
        org.junit.jupiter.api.Assertions.assertInstanceOf(WorkOrderUpdatedEvent.class, captor.getValue());
        verify(workOrderGateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lancar excecao quando ordem de servico nao for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        when(workOrderGateway.findById(workOrderId)).thenReturn(java.util.Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new RemoveMaterialItemFromWorkOrderCommand(workOrderId, materialId)));

        verify(workOrderGateway).findById(workOrderId);
        verifyNoInteractions(softDeleteStockUseCase, materialItemGateway);
    }
}
