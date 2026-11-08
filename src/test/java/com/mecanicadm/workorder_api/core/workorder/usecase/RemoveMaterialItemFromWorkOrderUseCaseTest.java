package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.usecase.SoftDeleteStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.SoftDeleteStockCommand;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveMaterialItemFromWorkOrderCommand;
import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderUpdatedEvent;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        Mockito.lenient().when(workOrderGateway.findDetailsById(ArgumentMatchers.any())).thenAnswer(invocation ->
                Optional.of(new WorkOrderDetails(invocation.getArgument(0), null, null, "descricao",
                        WorkOrderStatus.RECEIVED, null, null,
                        List.of(), List.of())));
        useCase = new RemoveMaterialItemFromWorkOrderUseCase(workOrderGateway, materialItemGateway, softDeleteStockUseCase, publisher);
    }

    @Test
    @DisplayName("Deve remover o item de material com sucesso")
    void shouldRemoveMaterialItemSuccessfully() {
        UUID materialId = UUID.randomUUID();
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        UUID workOrderId = workOrder.getId();
        when(workOrderGateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        useCase.execute(new RemoveMaterialItemFromWorkOrderCommand(workOrderId, materialId));

        verify(workOrderGateway, times(2)).findById(workOrderId);
        verify(softDeleteStockUseCase).execute(new SoftDeleteStockCommand(materialId, workOrderId));
        verify(materialItemGateway).deleteByWorkOrderIdAndMaterialId(workOrderId, materialId);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(ArgumentMatchers.any(), captor.capture());
        Assertions.assertInstanceOf(WorkOrderUpdatedEvent.class, captor.getValue());
        verify(workOrderGateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lancar excecao quando ordem de servico nao for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        when(workOrderGateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new RemoveMaterialItemFromWorkOrderCommand(workOrderId, materialId)));

        verify(workOrderGateway).findById(workOrderId);
        verifyNoInteractions(softDeleteStockUseCase, materialItemGateway);
    }
}
