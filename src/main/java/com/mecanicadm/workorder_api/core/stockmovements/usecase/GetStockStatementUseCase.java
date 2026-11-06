package com.mecanicadm.workorder_api.core.stockmovements.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsFilter;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.dto.StockStatement;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.query.GetStockStatementQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.List;

public class GetStockStatementUseCase implements UseCase<GetStockStatementQuery, StockStatement> {

    private final StockMovementsGateway gateway;

    public GetStockStatementUseCase(StockMovementsGateway gateway) {
        this.gateway = gateway;
    }

    public StockStatement execute(GetStockStatementQuery query) {
        Integer currentBalance = gateway.getCurrentBalanceByMaterialId(query.materialId());

        StockMovementsFilter filter = new StockMovementsFilter(query.materialId());
        List<StockMovements> movements = gateway.findAllByMaterialIdOrderByDateCreatedDesc(filter);

        return new StockStatement(query.materialId(), currentBalance, movements);
    }
}
