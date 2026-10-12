package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderJpaEntity;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

public class WorkOrderJpaMapper {

    private WorkOrderJpaMapper() {
    }

    public static WorkOrder toDomain(WorkOrderJpaEntity entity, Set<WorkOrderLaborItem> laborItems, Set<WorkOrderMaterialItem> materialItems) {
        if (isNull(entity)) return null;
        return WorkOrder.restore(
                entity.getId(),
                entity.getClientId(),
                entity.getVehicleId(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getExecutionStartAt().orElse(null),
                entity.getExecutionEndAt().orElse(null),
                laborItems,
                materialItems,
                entity.getDateCreated(),
                entity.getDateUpdated(),
                entity.getDeletedAt()
        );
    }

    public static WorkOrder toDomainLight(WorkOrderJpaEntity entity) {
        if (isNull(entity)) return null;
        return WorkOrder.restore(
                entity.getId(),
                entity.getClientId(),
                entity.getVehicleId(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getExecutionStartAt().orElse(null),
                entity.getExecutionEndAt().orElse(null),
                new HashSet<>(),
                new HashSet<>(),
                null,
                entity.getDateCreated(),
                entity.getDateUpdated(),
                entity.getDeletedAt()
        );
    }

    public static WorkOrderJpaEntity toEntity(WorkOrder domain) {
        if (isNull(domain)) return null;
        WorkOrderJpaEntity entity = new WorkOrderJpaEntity(
                domain.getId(),
                domain.getClientId(),
                domain.getVehicleId(),
                domain.getDescription(),
                domain.getStatus(),
                domain.getExecutionStartAt().orElse(null),
                domain.getExecutionEndAt().orElse(null)
        );
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateUpdated(domain.getDateUpdated());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }
}