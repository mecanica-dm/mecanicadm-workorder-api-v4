package com.mecanicadm.workorder_api.core.stockmovements.domain.port;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockMovementsGateway {
    StockMovements create(StockMovements stockMovements);

    StockMovements update(StockMovements stockMovements);

    List<StockMovements> findAllByMaterialIdOrderByDateCreatedDesc(StockMovementsFilter query);

    int getCurrentBalanceByMaterialId(UUID materialId);

    Optional<StockMovements> findByMaterialIdAndWorkOrderId(UUID materialId, UUID workOrderId);

    List<StockMovements> findAllByMaterialIdAndWorkOrderId(UUID materialId, UUID workOrderId);

    Optional<StockMovements> findByMaterialId(UUID materialId);
}
