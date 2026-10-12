package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderBudgetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkOrderBudgetJpaRepository extends JpaRepository<WorkOrderBudgetJpaEntity, UUID> {
}
