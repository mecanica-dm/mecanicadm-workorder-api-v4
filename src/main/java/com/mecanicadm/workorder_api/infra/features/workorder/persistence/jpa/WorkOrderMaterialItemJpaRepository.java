package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderMaterialItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkOrderMaterialItemJpaRepository extends JpaRepository<WorkOrderMaterialItemJpaEntity, UUID> {

    List<WorkOrderMaterialItemJpaEntity> findByWorkOrderId(UUID workOrderId);

    Optional<WorkOrderMaterialItemJpaEntity> findByWorkOrderIdAndMaterialId(UUID workOrderId, UUID materialId);

    void deleteByWorkOrderIdAndMaterialId(UUID workOrderId, UUID materialId);
}
