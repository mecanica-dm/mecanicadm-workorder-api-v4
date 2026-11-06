package com.mecanicadm.workorder_api.infra.messaging.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox")
public class OutboxJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Generated(event = EventType.INSERT)
    @Column(name = "sequence", insertable = false, updatable = false)
    private Long sequence;

    @Column(name = "aggregate_id", nullable = false, updatable = false)
    private UUID aggregateId;

    @Column(name = "type", nullable = false, updatable = false)
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, updatable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "last_error")
    private String lastError;

    protected OutboxJpaEntity() {
    }

    public OutboxJpaEntity(UUID id, UUID aggregateId, String type, String payload, Instant occurredAt) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.type = type;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    public void markPublished(Instant publishedAt) {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = publishedAt;
        this.lastError = null;
    }

    public void markFailed(String error) {
        this.status = OutboxStatus.FAILED;
        this.lastError = error;
    }

    public UUID getId() {
        return id;
    }

    public Long getSequence() {
        return sequence;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public String getLastError() {
        return lastError;
    }
}
