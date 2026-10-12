package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderLaborItemJpaEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class WorkOrderLaborItemJpaMapper {

    private WorkOrderLaborItemJpaMapper() {
    }

    public static WorkOrderLaborItem toDomain(WorkOrderLaborItemJpaEntity entity) {
        if (isNull(entity)) return null;
        return WorkOrderLaborItem.restore(
                entity.getId(),
                entity.getWorkOrderId(),
                entity.getLaborId(),
                entity.getExecutionStartAt(),
                entity.getExecutionEndAt(),
                entity.getStatus()
        );
    }

    public static WorkOrderLaborItemJpaEntity toEntity(WorkOrderLaborItem domain) {
        if (isNull(domain)) return null;
        return new WorkOrderLaborItemJpaEntity(
                domain.getId(),
                domain.getWorkOrderId(),
                domain.getLaborId(),
                domain.getExecutionStartAt(),
                domain.getExecutionEndAt(),
                domain.getStatus()
        );
    }

    public static Set<WorkOrderLaborItem> toDomainSet(Collection<WorkOrderLaborItemJpaEntity> entities) {
        if (isNull(entities)) return Collections.emptySet();
        return entities.stream().map(WorkOrderLaborItemJpaMapper::toDomain).collect(Collectors.toSet());
    }
}