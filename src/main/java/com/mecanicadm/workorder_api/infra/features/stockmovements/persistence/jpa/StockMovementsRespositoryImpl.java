package com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.jpa;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsFilter;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.entity.StockMovementsJpaEntity;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Repository
public class StockMovementsRespositoryImpl implements StockMovementsGateway {

    private final StockMovementsJpaRepository jpaRepository;

    public StockMovementsRespositoryImpl(StockMovementsJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StockMovements create(StockMovements stockMovements) {
        if (isNull(stockMovements)) {
            throw new TechnicalException("error.technical.entity.null", "StockMovements", "criação");
        }
        StockMovementsJpaEntity entity = StockMovementsJpaMapper.toEntity(stockMovements);
        StockMovementsJpaEntity saved = jpaRepository.save(entity);
        return StockMovementsJpaMapper.toDomain(saved);
    }

    @Override
    public StockMovements update(StockMovements stockMovements) {
        if (isNull(stockMovements)) {
            throw new TechnicalException("error.technical.entity.null", "StockMovements", "atualização");
        }
        StockMovementsJpaEntity entity = StockMovementsJpaMapper.toEntity(stockMovements);
        StockMovementsJpaEntity saved = jpaRepository.save(entity);
        return StockMovementsJpaMapper.toDomain(saved);
    }

    @Override
    public List<StockMovements> findAllByMaterialIdOrderByDateCreatedDesc(StockMovementsFilter query) {
        return jpaRepository.findAllByMaterialIdOrderByDateCreatedDesc(query.materialId()).stream().map(StockMovementsJpaMapper::toDomain).toList();
    }

    @Override
    public int getCurrentBalanceByMaterialId(UUID materialId) {
        Integer balance = jpaRepository.getCurrentBalanceByMaterialId(materialId);
        return balance != null ? balance : 0;
    }

    @Override
    public Optional<StockMovements> findByMaterialIdAndWorkOrderId(UUID materialId, UUID workOrderId) {
        return jpaRepository.findByMaterialIdAndWorkOrderId(materialId, workOrderId).map(StockMovementsJpaMapper::toDomain);
    }

    @Override
    public List<StockMovements> findAllByMaterialIdAndWorkOrderId(UUID materialId, UUID workOrderId) {
        return jpaRepository.findAllByMaterialIdAndWorkOrderId(materialId, workOrderId)
                .stream()
                .map(StockMovementsJpaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<StockMovements> findByMaterialId(UUID materialId) {
        return jpaRepository.findByMaterialId(materialId).map(StockMovementsJpaMapper::toDomain);
    }
}
