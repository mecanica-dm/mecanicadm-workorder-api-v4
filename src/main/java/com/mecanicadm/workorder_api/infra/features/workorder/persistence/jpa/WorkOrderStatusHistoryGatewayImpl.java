package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderStatusChange;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderStatusHistoryGateway;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderStatusHistoryJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class WorkOrderStatusHistoryGatewayImpl implements WorkOrderStatusHistoryGateway {

    private final WorkOrderStatusHistoryJpaRepository repository;

    public WorkOrderStatusHistoryGatewayImpl(WorkOrderStatusHistoryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<WorkOrderStatusChange> findByWorkOrderId(UUID workOrderId) {
        return repository.findByWorkOrderIdOrderByChangedAtAscIdAsc(workOrderId).stream()
                .map(WorkOrderStatusHistoryGatewayImpl::toDomain)
                .toList();
    }

    static WorkOrderStatusChange toDomain(WorkOrderStatusHistoryJpaEntity entity) {
        return new WorkOrderStatusChange(
                entity.getFromStatus() == null ? null : WorkOrderStatus.valueOf(entity.getFromStatus()),
                WorkOrderStatus.valueOf(entity.getToStatus()),
                entity.getReason(),
                entity.getChangedBy(),
                entity.getChangedAt());
    }

    static WorkOrderStatusHistoryJpaEntity toEntity(UUID workOrderId, WorkOrderStatusChange change) {
        return new WorkOrderStatusHistoryJpaEntity(
                workOrderId,
                change.fromStatus() == null ? null : change.fromStatus().name(),
                change.toStatus().name(),
                change.reason(),
                change.changedBy(),
                change.changedAt());
    }
}
