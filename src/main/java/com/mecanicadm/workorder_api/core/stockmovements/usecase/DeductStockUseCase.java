package com.mecanicadm.workorder_api.core.stockmovements.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.exception.StockMovementsExceptions;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.DeductStockCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class DeductStockUseCase implements VoidUseCase<DeductStockCommand> {

    private final StockMovementsGateway gateway;

    public DeductStockUseCase(StockMovementsGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(DeductStockCommand cmd) {
        if (cmd.quantity() <= 0) {
            throw new StockMovementsExceptions.InvalidQuantity();
        }

        int currentBalance = gateway.getCurrentBalanceByMaterialId(cmd.materialId());
        if (currentBalance < cmd.quantity()) {
            throw new StockMovementsExceptions.InsufficientStock();
        }

        StockMovements stockMovements = StockMovements.recordReduction(cmd.materialId(), cmd.workOrderId(), cmd.quantity());
        gateway.create(stockMovements);
    }

}
