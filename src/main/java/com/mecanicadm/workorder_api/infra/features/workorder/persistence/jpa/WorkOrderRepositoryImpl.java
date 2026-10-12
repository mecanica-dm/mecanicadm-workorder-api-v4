package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.*;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderJpaEntity;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa.specification.WorkOrderSpecificationBuilder;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Repository
public class WorkOrderRepositoryImpl implements WorkOrderGateway {

    private final WorkOrderJpaRepository jpaRepository;
    private final WorkOrderLaborItemJpaRepository laborItemJpaRepository;
    private final WorkOrderMaterialItemJpaRepository materialItemJpaRepository;
    private final WorkOrderBudgetJpaRepository budgetJpaRepository;

    public WorkOrderRepositoryImpl(WorkOrderJpaRepository jpaRepository,
                                   WorkOrderLaborItemJpaRepository laborItemJpaRepository,
                                   WorkOrderMaterialItemJpaRepository materialItemJpaRepository,
                                   WorkOrderBudgetJpaRepository budgetJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.laborItemJpaRepository = laborItemJpaRepository;
        this.materialItemJpaRepository = materialItemJpaRepository;
        this.budgetJpaRepository = budgetJpaRepository;
    }

    @Override
    public WorkOrder create(WorkOrder workOrder) {
        if (isNull(workOrder)) {
            throw new TechnicalException("error.technical.entity.null", "WorkOrder", "criação");
        }
        WorkOrderJpaEntity entity = WorkOrderJpaMapper.toEntity(workOrder);
        entity = jpaRepository.save(entity);
        return WorkOrderJpaMapper.toDomainLight(entity);
    }

    @Override
    public WorkOrder update(WorkOrder workOrder) {
        if (isNull(workOrder)) {
            throw new TechnicalException("error.technical.entity.null", "WorkOrder", "atualização");
        }
        WorkOrderJpaEntity entity = WorkOrderJpaMapper.toEntity(workOrder);
        entity = jpaRepository.save(entity);
        return WorkOrderJpaMapper.toDomainLight(entity);
    }

    @Override
    public void saveBudget(WorkOrderBudget budget) {
        if (isNull(budget)) {
            throw new TechnicalException("error.technical.entity.null", "WorkOrderBudget", "salvar");
        }
        var entity = WorkOrderBudgetJpaMapper.toEntity(budget);
        budgetJpaRepository.save(entity);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Optional<WorkOrder> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainWithItems);
    }

    @Override
    public Optional<WorkOrder> findByIdWithItems(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainWithItems);
    }

    @Override
    public Optional<WorkOrderStatus> findStatusById(UUID id) {
        return jpaRepository.findStatusById(id);
    }

    private WorkOrder toDomainWithItems(WorkOrderJpaEntity entity) {
        var laborItems = WorkOrderLaborItemJpaMapper.toDomainSet(
                laborItemJpaRepository.findByWorkOrderId(entity.getId()));
        var materialItems = WorkOrderMaterialItemJpaMapper.toDomainSet(
                materialItemJpaRepository.findByWorkOrderId(entity.getId()));
        var budget = budgetJpaRepository.findById(entity.getId())
                .map(WorkOrderBudgetJpaMapper::toDomain).orElse(null);
        return WorkOrderJpaMapper.toDomain(entity, laborItems, materialItems, budget);
    }

    @Override
    public WorkOrderPageResult findAll(WorkOrderPageQuery query) {
        Specification<WorkOrderJpaEntity> spec = WorkOrderSpecificationBuilder.buildFilterSpecification(query.filter());

        Pageable pageable = PageRequest.of(query.page(), query.size(), buildSort(query));

        var page = jpaRepository.findAll(spec, pageable);
        return new WorkOrderPageResult(page.map(WorkOrderJpaMapper::toDomainLight).getContent(),
                page.getTotalElements()
        );
    }

    private Sort buildSort(WorkOrderPageQuery query) {
        List<Sort.Order> orders = query.sorts().stream()
                .map(c -> new Sort.Order(Sort.Direction.fromString(c.direction()), c.field()))
                .toList();
        return Sort.by(orders);
    }

    @Override
    public BigDecimal sumMaterialsTotalByWorkOrderId(UUID workOrderId) {
        return jpaRepository.sumMaterialsTotalByWorkOrderId(workOrderId);
    }

    @Override
    public BigDecimal sumLaborTotalByWorkOrderId(UUID workOrderId) {
        return jpaRepository.sumLaborTotalByWorkOrderId(workOrderId);
    }

    @Override
    public WorkOrderExecutionSummaryProjection getExecutionTimeSummary(LocalDateTime initialDateTime, LocalDateTime finalDateTimeExclusive) {
        return jpaRepository.getExecutionTimeSummary(initialDateTime, finalDateTimeExclusive);
    }

    @Override
    public WorkOrderExecutionDurationProjection getSlowestExecution(LocalDateTime initialDateTime, LocalDateTime finalDateTimeExclusive) {
        return jpaRepository.getSlowestExecution(initialDateTime, finalDateTimeExclusive);
    }

    @Override
    public WorkOrderExecutionDurationProjection getFastestExecution(LocalDateTime initialDateTime, LocalDateTime finalDateTimeExclusive) {
        return jpaRepository.getFastestExecution(initialDateTime, finalDateTimeExclusive);
    }

    @Override
    public Double getAverageLaborExecutionMinutes(LocalDateTime initialDateTime, LocalDateTime finalDateTimeExclusive) {
        return jpaRepository.getAverageLaborExecutionMinutes(initialDateTime, finalDateTimeExclusive);
    }
}
