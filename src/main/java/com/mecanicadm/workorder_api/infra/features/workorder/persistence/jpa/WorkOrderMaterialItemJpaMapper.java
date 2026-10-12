package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderMaterialItemJpaEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class WorkOrderMaterialItemJpaMapper {

    private WorkOrderMaterialItemJpaMapper() {
    }

    public static WorkOrderMaterialItem toDomain(WorkOrderMaterialItemJpaEntity entity) {
        if (isNull(entity)) return null;
        return WorkOrderMaterialItem.restore(
                entity.getId(),
                entity.getMaterialId(),
                entity.getQuantity()
        );
    }

    public static WorkOrderMaterialItemJpaEntity toEntity(WorkOrderMaterialItem domain, UUID workOrderId) {
        if (isNull(domain)) return null;
        return new WorkOrderMaterialItemJpaEntity(
                domain.getId(),
                workOrderId,
                domain.getMaterialId(),
                domain.getQuantity()
        );
    }

    public static Set<WorkOrderMaterialItem> toDomainSet(Collection<WorkOrderMaterialItemJpaEntity> entities) {
        if (isNull(entities)) return Collections.emptySet();
        return entities.stream().map(WorkOrderMaterialItemJpaMapper::toDomain).collect(Collectors.toSet());
    }
}
