package com.mecanicadm.workorder_api.infra.features.vehicle.persistence.jpa;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.infra.features.vehicle.persistence.entity.VehicleJpaEntity;

public class VehicleJpaMapper {

    public static Vehicle toDomain(VehicleJpaEntity entity) {
        return Vehicle.restore(
                entity.getModel(), entity.getLicensePlate(), entity.getBrand(), entity.getModelYear(),
                entity.getDeletedAt(), entity.getDateCreated(), entity.getDateUpdated()
        );
    }

    public static VehicleJpaEntity toEntity(Vehicle domain) {
        var entity = new VehicleJpaEntity(
                domain.getLicensePlate(), domain.getModel(), domain.getBrand(), domain.getModelYear()
        );
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateUpdated(domain.getDateUpdated());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }
}

