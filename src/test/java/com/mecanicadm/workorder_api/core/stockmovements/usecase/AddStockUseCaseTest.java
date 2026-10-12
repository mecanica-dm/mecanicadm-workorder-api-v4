package com.mecanicadm.workorder_api.core.stockmovements.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.exception.StockMovementsExceptions;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.AddStockCommand;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddStockUseCaseTest {

    @Mock
    private StockMovementsGateway gateway;

    @InjectMocks
    private AddStockUseCase addStockUseCase;

    @Test
    @DisplayName("Deve registrar adicao de estoque como novo movimento")
    void shouldRecordAdditionAsNewMovement() {
        UUID materialId = UUID.randomUUID();

        addStockUseCase.execute(new AddStockCommand(materialId, 10));

        verify(gateway).create(any(StockMovements.class));
    }

    @Test
    @DisplayName("Nao deve adicionar estoque com quantidade invalida")
    void shouldNotAddStockWithInvalidQuantity() {
        UUID materialId = UUID.randomUUID();

        AddStockCommand command = new AddStockCommand(materialId, 0);

        DomainExceptionCore exception = assertThrows(
                DomainExceptionCore.class,
                () -> addStockUseCase.execute(command)
        );

        assertAll(
                () -> assertInstanceOf(StockMovementsExceptions.InvalidQuantity.class, exception),
                () -> assertEquals("stock.quantity.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
        verify(gateway, never()).create(any());
    }
}
