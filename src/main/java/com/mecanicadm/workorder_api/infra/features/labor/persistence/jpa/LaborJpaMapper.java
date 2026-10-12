package com.mecanicadm.workorder_api.infra.features.labor.persistence.jpa;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.infra.features.labor.persistence.entity.LaborJpaEntity;

public class LaborJpaMapper {

    private LaborJpaMapper() {
    }

    public static Labor toDomain(LaborJpaEntity entity) {
        return Labor.restore(
                entity.getId(),
                entity.getName(),
                entity.getPrice(),
                entity.getDeletedAt(),
                entity.getDateCreated(),
                entity.getDateUpdated()
        );
    }

    public static LaborJpaEntity toEntity(Labor domain) {
        var entity = new LaborJpaEntity(domain.getId(), domain.getName(), domain.getPrice());
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateUpdated(domain.getDateUpdated());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }

}

