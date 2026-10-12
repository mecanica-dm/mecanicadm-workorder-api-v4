package com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.infra.audit.AuditEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "work_orders")
@SQLRestriction("deleted_at IS NULL")
public class WorkOrderJpaEntity extends AuditEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private WorkOrderStatus status;

    @Column(name = "execution_start_at")
    private LocalDateTime executionStartAt;

    @Column(name = "execution_end_at")
    private LocalDateTime executionEndAt;

    protected WorkOrderJpaEntity() {
    }

    public WorkOrderJpaEntity(UUID id, UUID clientId, String vehicleId, String description, WorkOrderStatus status, LocalDateTime executionStartAt, LocalDateTime executionEndAt) {
        this.id = id;
        this.clientId = clientId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.status = status;
        this.executionStartAt = executionStartAt;
        this.executionEndAt = executionEndAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public Optional<LocalDateTime> getExecutionStartAt() {
        return Optional.ofNullable(executionStartAt);
    }

    public Optional<LocalDateTime> getExecutionEndAt() {
        return Optional.ofNullable(executionEndAt);
    }
}
