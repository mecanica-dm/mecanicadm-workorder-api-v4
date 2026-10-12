package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.BudgetDecisionToken;
import com.mecanicadm.workorder_api.core.workorder.domain.port.BudgetDecisionTokenGateway;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BudgetDecisionTokenGatewayImpl implements BudgetDecisionTokenGateway {

    private final BudgetDecisionTokenJpaRepository jpaRepository;

    public BudgetDecisionTokenGatewayImpl(BudgetDecisionTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BudgetDecisionToken create(BudgetDecisionToken token) {
        var entity = BudgetDecisionTokenJpaMapper.toEntity(token);
        entity = jpaRepository.save(entity);
        return BudgetDecisionTokenJpaMapper.toDomain(entity);
    }

    @Override
    public Optional<BudgetDecisionToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(BudgetDecisionTokenJpaMapper::toDomain);
    }

    @Override
    public void update(BudgetDecisionToken token) {
        var entity = BudgetDecisionTokenJpaMapper.toEntity(token);
        jpaRepository.save(entity);
    }
}
