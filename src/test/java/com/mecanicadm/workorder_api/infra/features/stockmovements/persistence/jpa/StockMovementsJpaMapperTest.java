package com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.jpa;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.enums.MovementType;
import com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.entity.StockMovementsJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StockMovementsJpaMapperTest {

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio")
    void shouldMapToDomain() {
        var id = UUID.randomUUID();
        var materialId = UUID.randomUUID();
        var workOrderId = UUID.randomUUID();
        var now = LocalDateTime.now();
        var entity = new StockMovementsJpaEntity(id, materialId, workOrderId, 5, MovementType.REDUCTION);
        entity.setDateCreated(now);
        entity.setDateUpdated(now);
        entity.setDeletedAt(null);

        var domain = StockMovementsJpaMapper.toDomain(entity);

        assertEquals(id, domain.getId());
        assertEquals(materialId, domain.getMaterialId());
        assertEquals(workOrderId, domain.getWorkOrderId());
        assertEquals(5, domain.getQuantity());
        assertEquals(MovementType.REDUCTION, domain.getType());
        assertEquals(now, domain.getDateCreated());
        assertEquals(now, domain.getDateUpdated());
    }

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity")
    void shouldMapToEntity() {
        var materialId = UUID.randomUUID();
        var stock = StockMovements.recordAddition(materialId, 10);

        var entity = StockMovementsJpaMapper.toEntity(stock);

        assertEquals(stock.getId(), entity.getId());
        assertEquals(materialId, entity.getMaterialId());
        assertEquals(10, entity.getQuantity());
        assertEquals(MovementType.ADDITION, entity.getType());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
    }
}
