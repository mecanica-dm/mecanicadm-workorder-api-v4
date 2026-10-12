package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.BudgetDecisionToken;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.BudgetDecisionTokenJpaEntity;

import static java.util.Objects.isNull;

public class BudgetDecisionTokenJpaMapper {

    private BudgetDecisionTokenJpaMapper() {
    }

    public static BudgetDecisionToken toDomain(BudgetDecisionTokenJpaEntity entity) {
        if (isNull(entity)) return null;
        return BudgetDecisionToken.restore(
                entity.getId(),
                entity.getWorkOrderId(),
                entity.getToken(),
                entity.isUsed(),
                entity.getCreatedAt()
        );
    }

    public static BudgetDecisionTokenJpaEntity toEntity(BudgetDecisionToken domain) {
        if (isNull(domain)) return null;
        return new BudgetDecisionTokenJpaEntity(
                domain.getId(),
                domain.getWorkOrderId(),
                domain.getToken(),
                domain.isUsed(),
                domain.getCreatedAt()
        );
    }
}
