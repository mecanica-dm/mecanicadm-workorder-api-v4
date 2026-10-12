package com.mecanicadm.workorder_api.core.stockmovements.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.exception.StockMovementsExceptions;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.DeductStockCommand;
import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeductStockUseCaseTest {

    @Mock
    private StockMovementsGateway gateway;

    @InjectMocks
    private DeductStockUseCase deductStockUseCase;

    @Test
    @DisplayName("Deve registrar deducao como novo movimento quando houver saldo")
    void shouldRecordReductionAsNewMovementWhenHasBalance() {
        UUID materialId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        when(gateway.getCurrentBalanceByMaterialId(materialId)).thenReturn(20);

        deductStockUseCase.execute(new DeductStockCommand(materialId, workOrderId, 5));

        verify(gateway).create(any(StockMovements.class));
    }

    @Test
    @DisplayName("Nao deve deduzir quando quantidade for invalida")
    void shouldNotDeductWithInvalidQuantity() {
        UUID materialId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();

        DeductStockCommand command = new DeductStockCommand(materialId, workOrderId, 0);

        DomainExceptionCore exception = assertThrows(
                DomainExceptionCore.class,
                () -> deductStockUseCase.execute(command)
        );

        assertAll(
                () -> assertInstanceOf(StockMovementsExceptions.InvalidQuantity.class, exception),
                () -> assertEquals("stock.quantity.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
        verify(gateway, never()).create(any());
    }

    @Test
    @DisplayName("Nao deve deduzir quando saldo for insuficiente")
    void shouldNotDeductWhenStockIsInsufficient() {
        UUID materialId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        when(gateway.getCurrentBalanceByMaterialId(materialId)).thenReturn(3);

        DeductStockCommand command = new DeductStockCommand(materialId, workOrderId, 5);

        DomainExceptionCore exception = assertThrows(
                DomainExceptionCore.class,
                () -> deductStockUseCase.execute(command)
        );

        assertAll(
                () -> assertInstanceOf(StockMovementsExceptions.InsufficientStock.class, exception),
                () -> assertEquals("stock.quantity.insufficient", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
        verify(gateway, never()).create(any());
    }

    @Test
    @DisplayName("Deve consultar saldo antes de deduzir")
    void shouldQueryCurrentBalanceBeforeDeduction() {
        UUID materialId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        when(gateway.getCurrentBalanceByMaterialId(materialId)).thenReturn(10);

        deductStockUseCase.execute(new DeductStockCommand(materialId, workOrderId, 5));

        verify(gateway).getCurrentBalanceByMaterialId(materialId);
        verify(gateway).create(any(StockMovements.class));
    }
}
