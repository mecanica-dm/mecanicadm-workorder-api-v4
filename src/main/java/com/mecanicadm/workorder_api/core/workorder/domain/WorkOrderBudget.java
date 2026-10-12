package com.mecanicadm.workorder_api.core.workorder.domain;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class WorkOrderBudget {

    private UUID workOrderId;

    private BigDecimal totalPrice;

    private WorkOrderBudgetStatus status;

    private String observation;

    protected WorkOrderBudget() {
    }

    private WorkOrderBudget(WorkOrder workOrder, BigDecimal totalPrice) {
        this.workOrderId = requireNonNull(workOrder).getId();
        this.totalPrice = requireNonNull(totalPrice);
        this.status = WorkOrderBudgetStatus.PENDING;
        validate();
    }

    private WorkOrderBudget(UUID workOrderId, BigDecimal totalPrice, WorkOrderBudgetStatus status, String observation) {
        this.workOrderId = workOrderId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.observation = observation;
        validate();
    }

    public static WorkOrderBudget create(WorkOrder workOrder, BigDecimal totalPrice) {
        return new WorkOrderBudget(workOrder, totalPrice);
    }

    public static WorkOrderBudget restore(UUID workOrderId, BigDecimal totalPrice, WorkOrderBudgetStatus status, String observation) {
        return new WorkOrderBudget(workOrderId, totalPrice, status, observation);
    }

    public void updateTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = requireNonNull(totalPrice);
        this.status = WorkOrderBudgetStatus.PENDING;
        this.observation = null;
        validate();
    }

    public void send() {
        this.status = WorkOrderBudgetStatus.WAITING_DECISION;
    }

    public void approve(String note) {
        this.observation = note;
        this.status = WorkOrderBudgetStatus.APPROVED;
    }

    public void reject(String reason, boolean needsRevision) {
        this.observation = requireNonNull(reason);
        this.status = needsRevision ? WorkOrderBudgetStatus.CHANGES_REQUESTED : WorkOrderBudgetStatus.REJECTED;
    }

    private void validate() {
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WorkOrderExceptions.BudgetTotalPriceInvalid();
        }
        if (status == null) {
            throw new WorkOrderExceptions.BudgetStatusInvalid();
        }
    }

    public UUID getWorkOrderId() {
        return workOrderId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public WorkOrderBudgetStatus getStatus() {
        return status;
    }

    public String getObservation() {
        return observation;
    }
}
