package com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "budget_decision_tokens")
public class BudgetDecisionTokenJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "work_order_id", nullable = false)
    private UUID workOrderId;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected BudgetDecisionTokenJpaEntity() {
    }

    public BudgetDecisionTokenJpaEntity(UUID id, UUID workOrderId, String token, boolean used, LocalDateTime createdAt) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.token = token;
        this.used = used;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getWorkOrderId() { return workOrderId; }
    public String getToken() { return token; }
    public boolean isUsed() { return used; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUsed(boolean used) { this.used = used; }
}
