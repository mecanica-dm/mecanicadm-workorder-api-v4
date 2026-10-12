package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Repository
public class WorkOrderMaterialItemRepositoryImpl implements WorkOrderMaterialItemGateway {

    private final WorkOrderMaterialItemJpaRepository jpaRepository;

    public WorkOrderMaterialItemRepositoryImpl(WorkOrderMaterialItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WorkOrderMaterialItem create(WorkOrderMaterialItem materialItem, UUID workOrderId) {
        if (isNull(materialItem)) {
            throw new TechnicalException("error.technical.entity.null", "WorkOrderMaterialItem", "criação");
        }
        var entity = WorkOrderMaterialItemJpaMapper.toEntity(materialItem, workOrderId);
        var saved = jpaRepository.save(entity);
        return WorkOrderMaterialItemJpaMapper.toDomain(saved);
    }

    @Override
    public WorkOrderMaterialItem update(WorkOrderMaterialItem materialItem, UUID workOrderId) {
        if (isNull(materialItem)) {
            throw new TechnicalException("error.technical.entity.null", "WorkOrderMaterialItem", "atualização");
        }
        var entity = WorkOrderMaterialItemJpaMapper.toEntity(materialItem, workOrderId);
        var saved = jpaRepository.save(entity);
        return WorkOrderMaterialItemJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<WorkOrderMaterialItem> findByWorkOrderIdAndMaterialId(UUID workOrderId, UUID materialId) {
        return jpaRepository.findByWorkOrderIdAndMaterialId(workOrderId, materialId)
                .map(WorkOrderMaterialItemJpaMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteByWorkOrderIdAndMaterialId(UUID workOrderId, UUID materialId) {
        jpaRepository.deleteByWorkOrderIdAndMaterialId(workOrderId, materialId);
    }
}
