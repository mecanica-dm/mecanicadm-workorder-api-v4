package com.mecanicadm.workorder_api.core.stockmovements.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.exception.StockMovementsExceptions;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.SoftDeleteStockCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import java.util.List;

public class SoftDeleteStockUseCase implements VoidUseCase<SoftDeleteStockCommand> {

    private final StockMovementsGateway gateway;

    public SoftDeleteStockUseCase(StockMovementsGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(SoftDeleteStockCommand cmd) {
        List<StockMovements> stocks = gateway.findAllByMaterialIdAndWorkOrderId(cmd.materialId(), cmd.workOrderId());
        if (stocks.isEmpty()) {
            throw new StockMovementsExceptions.NotFound();
        }
        stocks.forEach(stock -> {
            stock.delete();
            gateway.update(stock);
        });
    }
}
