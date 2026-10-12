package com.mecanicadm.workorder_api.core.stockmovements.domain;

import com.mecanicadm.workorder_api.core.stockmovements.domain.enums.MovementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StockMovementsTest {

    @Test
    @DisplayName("Deve criar uma movimentação de adição com sucesso")
    void shouldCreateAdditionMovementSuccessfully() {
        UUID materialId = UUID.randomUUID();
        Integer quantity = 10;

        StockMovements movement = StockMovements.recordAddition(materialId, quantity);

        assertNotNull(movement);
        assertEquals(materialId, movement.getMaterialId());
        assertEquals(quantity, movement.getQuantity());
        assertEquals(MovementType.ADDITION, movement.getType());
    }

    @Test
    @DisplayName("Deve criar uma movimentação de redução com sucesso")
    void shouldCreateReductionMovementSuccessfully() {
        UUID materialId = UUID.randomUUID();
        Integer quantity = 5;

        StockMovements movement = StockMovements.recordReduction(materialId, null, quantity);

        assertNotNull(movement);
        assertEquals(materialId, movement.getMaterialId());
        assertEquals(quantity, movement.getQuantity());
        assertEquals(MovementType.REDUCTION, movement.getType());
    }

    @Test
    @DisplayName("Deve restaurar uma movimentação a partir de dados existentes")
    void shouldRestoreMovement() {
        var id = UUID.randomUUID();
        var materialId = UUID.randomUUID();
        var workOrderId = UUID.randomUUID();
        var now = LocalDateTime.now();

        var movement = StockMovements.restore(id, materialId, workOrderId, 5, MovementType.REDUCTION, null, now, now);

        assertEquals(id, movement.getId());
        assertEquals(materialId, movement.getMaterialId());
        assertEquals(workOrderId, movement.getWorkOrderId());
        assertEquals(5, movement.getQuantity());
        assertEquals(MovementType.REDUCTION, movement.getType());
        assertEquals(now, movement.getDateCreated());
        assertEquals(now, movement.getDateUpdated());
    }
}
