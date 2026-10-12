package com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.jpa;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.entity.StockMovementsJpaEntity;

public class StockMovementsJpaMapper {

    public static StockMovements toDomain(StockMovementsJpaEntity entity) {
        return StockMovements.restore(
                entity.getId(), entity.getMaterialId(), entity.getWorkOrderId(),
                entity.getQuantity(), entity.getType(),
                entity.getDeletedAt(), entity.getDateCreated(), entity.getDateUpdated()
        );
    }

    public static StockMovementsJpaEntity toEntity(StockMovements domain) {
        var entity = new StockMovementsJpaEntity(
                domain.getId(), domain.getMaterialId(), domain.getWorkOrderId(),
                domain.getQuantity(), domain.getType()
        );
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateUpdated(domain.getDateUpdated());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }
}
