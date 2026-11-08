package com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.LaborExecutionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_order_labor_items")
public class WorkOrderLaborItemJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "work_order_id", nullable = false)
    private UUID workOrderId;

    @Column(name = "labor_id", nullable = false)
    private UUID laborId;

    @Column(name = "execution_start_at")
    private LocalDateTime executionStartAt;

    @Column(name = "execution_end_at")
    private LocalDateTime executionEndAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LaborExecutionStatus status;

    protected WorkOrderLaborItemJpaEntity() {
    }

    public WorkOrderLaborItemJpaEntity(UUID id, UUID workOrderId, UUID laborId, LocalDateTime executionStartAt, LocalDateTime executionEndAt, LaborExecutionStatus status) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.laborId = laborId;
        this.executionStartAt = executionStartAt;
        this.executionEndAt = executionEndAt;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorkOrderId() {
        return workOrderId;
    }

    public UUID getLaborId() {
        return laborId;
    }

    public LaborExecutionStatus getStatus() {
        return status;
    }

    public LocalDateTime getExecutionStartAt() {
        return executionStartAt;
    }

    public LocalDateTime getExecutionEndAt() {
        return executionEndAt;
    }
}
