package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.usecase.SoftDeleteStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.SoftDeleteStockCommand;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveMaterialItemFromWorkOrderCommand;
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

    private RemoveMaterialItemFromWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoveMaterialItemFromWorkOrderUseCase(workOrderGateway, materialItemGateway, softDeleteStockUseCase);
    }

    @Test
    @DisplayName("Deve remover o item de material com sucesso")
    void shouldRemoveMaterialItemSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        when(workOrderGateway.existsById(workOrderId)).thenReturn(true);

        useCase.execute(new RemoveMaterialItemFromWorkOrderCommand(workOrderId, materialId));

        verify(workOrderGateway).existsById(workOrderId);
        verify(softDeleteStockUseCase).execute(new SoftDeleteStockCommand(materialId, workOrderId));
        verify(materialItemGateway).deleteByWorkOrderIdAndMaterialId(workOrderId, materialId);
    }

    @Test
    @DisplayName("Deve lancar excecao quando ordem de servico nao for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        when(workOrderGateway.existsById(workOrderId)).thenReturn(false);

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new RemoveMaterialItemFromWorkOrderCommand(workOrderId, materialId)));

        verify(workOrderGateway).existsById(workOrderId);
        verifyNoInteractions(softDeleteStockUseCase, materialItemGateway);
    }
}
