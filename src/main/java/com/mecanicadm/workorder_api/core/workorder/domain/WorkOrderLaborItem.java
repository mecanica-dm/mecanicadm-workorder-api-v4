package com.mecanicadm.workorder_api.core.workorder.domain;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.LaborExecutionStatus;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class WorkOrderLaborItem {

    private UUID id;

    private UUID workOrderId;

    private UUID laborId;

    private LocalDateTime executionStartAt;

    private LocalDateTime executionEndAt;

    private LaborExecutionStatus status;

    protected WorkOrderLaborItem() {
    }

    private WorkOrderLaborItem(UUID laborId, UUID workOrderId) {
        this.id = UUID.randomUUID();
        this.laborId = requireNonNull(laborId);
        this.workOrderId = requireNonNull(workOrderId);
        this.status = LaborExecutionStatus.AWAITING_EXECUTION;
        validate();
    }

    private WorkOrderLaborItem(UUID id, UUID workOrderId, UUID laborId, LocalDateTime executionStartAt, LocalDateTime executionEndAt, LaborExecutionStatus status) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.laborId = laborId;
        this.executionStartAt = executionStartAt;
        this.executionEndAt = executionEndAt;
        this.status = status;
        validate();
    }

    public static WorkOrderLaborItem create(UUID laborId, UUID workOrderId) {
        return new WorkOrderLaborItem(laborId, workOrderId);
    }

    public static WorkOrderLaborItem restore(UUID id, UUID workOrderId, UUID laborId, LocalDateTime executionStartAt, LocalDateTime executionEndAt, LaborExecutionStatus status) {
        return new WorkOrderLaborItem(id, workOrderId, laborId, executionStartAt, executionEndAt, status);
    }

    public void startExecution() {
        if (this.status != LaborExecutionStatus.AWAITING_EXECUTION) {
            throw new WorkOrderExceptions.InvalidLaborStatusTransition(this.status.name(), LaborExecutionStatus.IN_EXECUTION.name());
        }
        this.status = LaborExecutionStatus.IN_EXECUTION;
        this.executionStartAt = LocalDateTime.now();
    }

    public void finishExecution() {
        if (this.status != LaborExecutionStatus.IN_EXECUTION) {
            throw new WorkOrderExceptions.InvalidLaborStatusTransition(this.status.name(), LaborExecutionStatus.EXECUTION_COMPLETED.name());
        }
        this.status = LaborExecutionStatus.EXECUTION_COMPLETED;
        this.executionEndAt = LocalDateTime.now();
    }

    private void validate() {
        if (laborId == null) {
            throw new WorkOrderExceptions.LaborIdRequired();
        }
        if (workOrderId == null) {
            throw new WorkOrderExceptions.WorkOrderIdRequired();
        }
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
