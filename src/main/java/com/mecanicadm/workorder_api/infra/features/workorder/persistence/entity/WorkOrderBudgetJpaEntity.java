package com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "work_order_budgets")
public class WorkOrderBudgetJpaEntity {

    @Id
    @Column(name = "work_order_id", nullable = false, updatable = false)
    private UUID workOrderId;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkOrderBudgetStatus status;

    @Column(name = "observation")
    private String observation;

    protected WorkOrderBudgetJpaEntity() {
    }

    public WorkOrderBudgetJpaEntity(UUID workOrderId, BigDecimal totalPrice, WorkOrderBudgetStatus status, String observation) {
        this.workOrderId = workOrderId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.observation = observation;
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
