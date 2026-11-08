package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Repository
public class WorkOrderLaborItemRepositoryImpl implements WorkOrderLaborItemGateway {

    private final WorkOrderLaborItemJpaRepository jpaRepository;

    public WorkOrderLaborItemRepositoryImpl(WorkOrderLaborItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WorkOrderLaborItem create(WorkOrderLaborItem laborItem) {
        if (isNull(laborItem)) {
            throw new TechnicalException("error.technical.entity.null", "WorkOrderLaborItem", "criação");
        }
        var entity = WorkOrderLaborItemJpaMapper.toEntity(laborItem);
        var saved = jpaRepository.save(entity);
        return WorkOrderLaborItemJpaMapper.toDomain(saved);
    }

    @Override
    public WorkOrderLaborItem update(WorkOrderLaborItem laborItem) {
        if (isNull(laborItem)) {
            throw new TechnicalException("error.technical.entity.null", "WorkOrderLaborItem", "atualização");
        }
        var entity = WorkOrderLaborItemJpaMapper.toEntity(laborItem);
        var saved = jpaRepository.save(entity);
        return WorkOrderLaborItemJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<WorkOrderLaborItem> findById(UUID id) {
        return jpaRepository.findById(id).map(WorkOrderLaborItemJpaMapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(WorkOrderLaborItem laborItem) {
        if (isNull(laborItem)) {
            throw new TechnicalException("error.technical.entity.null", "WorkOrderLaborItem", "exclusão");
        }
        jpaRepository.deleteById(laborItem.getId());
    }
}
