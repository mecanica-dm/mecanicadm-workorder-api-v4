package com.mecanicadm.workorder_api.core.workorder.domain;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class BudgetDecisionToken {

    private UUID id;
    private UUID workOrderId;
    private String token;
    private boolean used;
    private LocalDateTime createdAt;

    private BudgetDecisionToken(UUID id, UUID workOrderId, String token, boolean used, LocalDateTime createdAt) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.token = token;
        this.used = used;
        this.createdAt = createdAt;
    }

    public static BudgetDecisionToken create(UUID workOrderId) {
        return new BudgetDecisionToken(
                UUID.randomUUID(),
                workOrderId,
                UUID.randomUUID().toString(),
                false,
                LocalDateTime.now(ZoneOffset.UTC)
        );
    }

    public static BudgetDecisionToken restore(UUID id, UUID workOrderId, String token, boolean used, LocalDateTime createdAt) {
        return new BudgetDecisionToken(id, workOrderId, token, used, createdAt);
    }

    public void markAsUsed() {
        this.used = true;
    }

    public boolean isExpired() {
        return createdAt.plusHours(24).isBefore(LocalDateTime.now(ZoneOffset.UTC));
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    public UUID getId() { return id; }
    public UUID getWorkOrderId() { return workOrderId; }
    public String getToken() { return token; }
    public boolean isUsed() { return used; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
