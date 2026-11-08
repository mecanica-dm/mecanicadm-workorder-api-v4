package com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.entity.StockMovementsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockMovementsJpaRepository extends JpaRepository<StockMovementsJpaEntity, UUID> {

    @Query("""
            SELECT COALESCE(SUM(
                CASE
                    WHEN sm.type = 'ADDITION' THEN sm.quantity
                    ELSE -sm.quantity
                END
            ), 0)
            FROM StockMovementsJpaEntity sm
            WHERE sm.materialId = :materialId
            """)
    Integer getCurrentBalanceByMaterialId(@Param("materialId") UUID materialId);

    Optional<StockMovementsJpaEntity> findByMaterialId(UUID materialId);

    Optional<StockMovementsJpaEntity> findByMaterialIdAndWorkOrderId(UUID materialId, UUID workOrderId);

    List<StockMovementsJpaEntity> findAllByMaterialIdAndWorkOrderId(UUID materialId, UUID workOrderId);

    List<StockMovementsJpaEntity> findAllByMaterialIdOrderByDateCreatedDesc(UUID materialId);
}
