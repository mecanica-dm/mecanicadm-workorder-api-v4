package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddMaterialToWorkOrderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

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

    private AddMaterialToWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddMaterialToWorkOrderUseCase(gateway, materialItemGateway, createWorkOrderMaterialItemUseCase);
    }

    @Test
    @DisplayName("Deve criar novo material item quando não existir")
    void shouldCreateNewMaterialItemWhenNotExists() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        int quantity = 2;
        when(gateway.existsById(workOrderId)).thenReturn(true);
        when(materialItemGateway.findByWorkOrderIdAndMaterialId(workOrderId, materialId)).thenReturn(Optional.empty());

        useCase.execute(new AddMaterialToWorkOrderCommand(workOrderId, materialId, quantity));

        verify(gateway).existsById(workOrderId);
        verify(materialItemGateway).findByWorkOrderIdAndMaterialId(workOrderId, materialId);
        verify(materialItemGateway).create(any(WorkOrderMaterialItem.class), eq(workOrderId));
    }

    @Test
    @DisplayName("Deve atualizar quantidade quando material item já existir")
    void shouldUpdateQuantityWhenMaterialItemExists() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        int quantity = 3;
        var existingItem = mock(WorkOrderMaterialItem.class);
        when(gateway.existsById(workOrderId)).thenReturn(true);
        when(materialItemGateway.findByWorkOrderIdAndMaterialId(workOrderId, materialId)).thenReturn(Optional.of(existingItem));

        useCase.execute(new AddMaterialToWorkOrderCommand(workOrderId, materialId, quantity));

        verify(gateway).existsById(workOrderId);
        verify(materialItemGateway).findByWorkOrderIdAndMaterialId(workOrderId, materialId);
        verify(existingItem).addQuantity(quantity);
        verify(materialItemGateway).update(existingItem, workOrderId);
        verify(materialItemGateway, never()).create(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        int quantity = 1;
        when(gateway.existsById(workOrderId)).thenReturn(false);

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new AddMaterialToWorkOrderCommand(workOrderId, materialId, quantity)));

        verify(gateway).existsById(workOrderId);
        verifyNoInteractions(materialItemGateway);
    }
}
