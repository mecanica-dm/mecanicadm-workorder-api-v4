package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.SoftDeleteWorkOrderCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftDeleteWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @Mock
    private WorkOrderMaterialItemGateway materialItemGateway;

    @Mock
    private StockMovementsGateway stockMovementsGateway;

    @InjectMocks
    private SoftDeleteWorkOrderUseCase useCase;

    @Test
    @DisplayName("Deve excluir logicamente uma ordem de serviço com itens de material")
    void shouldSoftDeleteWorkOrderWithMaterialItemsSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        UUID materialId = UUID.randomUUID();
        WorkOrderMaterialItem materialItem = mock(WorkOrderMaterialItem.class);
        StockMovements stock = mock(StockMovements.class);

        when(materialItem.getMaterialId()).thenReturn(materialId);
        when(workOrder.getMaterialItems()).thenReturn(Set.of(materialItem));
        when(workOrder.getId()).thenReturn(workOrderId);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(stockMovementsGateway.findByMaterialIdAndWorkOrderId(materialId, workOrderId))
                .thenReturn(Optional.of(stock));

        useCase.execute(new SoftDeleteWorkOrderCommand(workOrderId));

        verify(gateway).findById(workOrderId);
        verify(stockMovementsGateway).findByMaterialIdAndWorkOrderId(materialId, workOrderId);
        verify(stock).delete();
        verify(stockMovementsGateway).update(stock);
        verify(materialItemGateway).deleteByWorkOrderIdAndMaterialId(workOrderId, materialId);
        verify(workOrder).delete();
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve excluir logicamente quando stock movement nao existe")
    void shouldSoftDeleteWorkOrderWhenStockMovementDoesNotExist() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        UUID materialId = UUID.randomUUID();
        WorkOrderMaterialItem materialItem = mock(WorkOrderMaterialItem.class);

        when(materialItem.getMaterialId()).thenReturn(materialId);
        when(workOrder.getMaterialItems()).thenReturn(Set.of(materialItem));
        when(workOrder.getId()).thenReturn(workOrderId);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(stockMovementsGateway.findByMaterialIdAndWorkOrderId(materialId, workOrderId))
                .thenReturn(Optional.empty());

        useCase.execute(new SoftDeleteWorkOrderCommand(workOrderId));

        verify(gateway).findById(workOrderId);
        verify(stockMovementsGateway).findByMaterialIdAndWorkOrderId(materialId, workOrderId);
        verify(stockMovementsGateway, never()).update(any());
        verify(materialItemGateway).deleteByWorkOrderIdAndMaterialId(workOrderId, materialId);
        verify(workOrder).delete();
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lancar excecao quando a ordem de servico nao for encontrada para exclusao")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new SoftDeleteWorkOrderCommand(workOrderId)));

        verify(gateway).findById(workOrderId);
        verifyNoMoreInteractions(gateway);
    }
}
