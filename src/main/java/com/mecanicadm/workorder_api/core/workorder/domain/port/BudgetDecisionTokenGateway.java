package com.mecanicadm.workorder_api.core.workorder.domain.port;

import com.mecanicadm.workorder_api.core.workorder.domain.BudgetDecisionToken;

import java.util.Optional;

public interface BudgetDecisionTokenGateway {
    BudgetDecisionToken create(BudgetDecisionToken token);
    Optional<BudgetDecisionToken> findByToken(String token);
    void update(BudgetDecisionToken token);
}
