package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.BudgetDecisionTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetDecisionTokenJpaRepository extends JpaRepository<BudgetDecisionTokenJpaEntity, UUID> {
    Optional<BudgetDecisionTokenJpaEntity> findByToken(String token);
}
