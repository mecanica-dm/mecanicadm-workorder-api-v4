package com.mecanicadm.workorder_api.infra.audit;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AuditEntity {

    @Column(name = "date_created", updatable = false)
    protected LocalDateTime dateCreated;

    @Column(name = "date_updated")
    protected LocalDateTime dateUpdated;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
