package com.mecanicadm.workorder_api.core.stockmovements.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.exception.StockMovementsExceptions;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.AddStockCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class AddStockUseCase implements VoidUseCase<AddStockCommand> {

    private final StockMovementsGateway gateway;

    public AddStockUseCase(StockMovementsGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(AddStockCommand cmd) {
        if (cmd.quantity() <= 0) {
            throw new StockMovementsExceptions.InvalidQuantity();
        }

        StockMovements stockMovements = StockMovements.recordAddition(cmd.materialId(), cmd.quantity());
        gateway.create(stockMovements);
    }
}
