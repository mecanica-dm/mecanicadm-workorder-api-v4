package com.mecanicadm.workorder_api.core.stockmovements.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.exception.StockMovementsExceptions;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.SoftDeleteStockCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftDeleteStockUseCaseTest {

    @Mock
    private StockMovementsGateway gateway;

    private SoftDeleteStockUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SoftDeleteStockUseCase(gateway);
    }

    @Test
    @DisplayName("Deve realizar soft delete de múltiplos movimentos de estoque com sucesso")
    void shouldSoftDeleteAllStockMovementsSuccessfully() {
        var materialId = UUID.randomUUID();
        var workOrderId = UUID.randomUUID();
        var command = new SoftDeleteStockCommand(materialId, workOrderId);
        var stock1 = mock(StockMovements.class);
        var stock2 = mock(StockMovements.class);
        when(gateway.findAllByMaterialIdAndWorkOrderId(materialId, workOrderId)).thenReturn(List.of(stock1, stock2));

        useCase.execute(command);

        verify(gateway).findAllByMaterialIdAndWorkOrderId(materialId, workOrderId);
        verify(stock1).delete();
        verify(gateway).update(stock1);
        verify(stock2).delete();
        verify(gateway).update(stock2);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir movimento inexistente")
    void shouldThrowExceptionWhenStockNotFound() {
        var materialId = UUID.randomUUID();
        var workOrderId = UUID.randomUUID();
        var command = new SoftDeleteStockCommand(materialId, workOrderId);
        when(gateway.findAllByMaterialIdAndWorkOrderId(materialId, workOrderId)).thenReturn(List.of());

        assertThrows(StockMovementsExceptions.NotFound.class, () -> useCase.execute(command));

        verify(gateway).findAllByMaterialIdAndWorkOrderId(materialId, workOrderId);
        verify(gateway, never()).update(any());
    }
}
