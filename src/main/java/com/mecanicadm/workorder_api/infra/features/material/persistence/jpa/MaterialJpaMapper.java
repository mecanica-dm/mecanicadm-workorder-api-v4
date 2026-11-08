package com.mecanicadm.workorder_api.infra.features.material.persistence.jpa;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.infra.features.material.persistence.entity.MaterialJpaEntity;

public class MaterialJpaMapper {

    private MaterialJpaMapper() {
    }

    public static Material toDomain(MaterialJpaEntity entity) {
        return Material.restore(
                entity.getId(), entity.getName(), entity.getBrand(), entity.getDescription(),
                entity.getPrice(), entity.getType(),
                entity.getDeletedAt(), entity.getDateCreated(), entity.getDateUpdated()
        );
    }

    public static MaterialJpaEntity toEntity(Material domain) {
        var entity = new MaterialJpaEntity(
                domain.getId(), domain.getName(), domain.getBrand(),
                domain.getDescription(), domain.getPrice(), domain.getType()
        );
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateUpdated(domain.getDateUpdated());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }

}
