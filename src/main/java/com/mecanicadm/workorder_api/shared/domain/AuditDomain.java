package com.mecanicadm.workorder_api.shared.domain;

import java.time.Clock;
import java.time.LocalDateTime;

public abstract class AuditDomain {

    protected LocalDateTime dateCreated;
    protected LocalDateTime dateUpdated;
    protected LocalDateTime deletedAt;

    protected AuditDomain() {
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void create() {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        this.dateCreated = now;
        this.dateUpdated = now;
    }

    public void update() {
        this.dateUpdated = LocalDateTime.now(Clock.systemDefaultZone());
    }

    public void delete() {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        this.deletedAt = now;
        this.dateUpdated = now;
    }
}
