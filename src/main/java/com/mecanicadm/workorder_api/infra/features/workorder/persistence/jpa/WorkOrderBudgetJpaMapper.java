package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderBudgetJpaEntity;

import static java.util.Objects.isNull;

public class WorkOrderBudgetJpaMapper {

    private WorkOrderBudgetJpaMapper() {
    }

    public static WorkOrderBudget toDomain(WorkOrderBudgetJpaEntity entity) {
        if (isNull(entity)) return null;
        return WorkOrderBudget.restore(
                entity.getWorkOrderId(),
                entity.getTotalPrice(),
                entity.getStatus(),
                entity.getObservation()
        );
    }

    public static WorkOrderBudgetJpaEntity toEntity(WorkOrderBudget domain) {
        if (isNull(domain)) return null;
        return new WorkOrderBudgetJpaEntity(
                domain.getWorkOrderId(),
                domain.getTotalPrice(),
                domain.getStatus(),
                domain.getObservation()
        );
    }
}
