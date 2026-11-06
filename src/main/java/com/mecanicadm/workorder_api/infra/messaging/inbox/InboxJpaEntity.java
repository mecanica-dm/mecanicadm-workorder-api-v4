package com.mecanicadm.workorder_api.infra.messaging.inbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox")
public class InboxJpaEntity {

    @Id
    @Column(name = "message_id", nullable = false, updatable = false)
    private UUID messageId;

    @Column(name = "message_type", nullable = false, updatable = false)
    private String messageType;

    @Column(name = "work_order_id", updatable = false)
    private UUID workOrderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InboxStatus status;

    @Column(name = "error")
    private String error;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected InboxJpaEntity() {
    }

    public InboxJpaEntity(UUID messageId, String messageType, UUID workOrderId, InboxStatus status, Instant processedAt) {
        this.messageId = messageId;
        this.messageType = messageType;
        this.workOrderId = workOrderId;
        this.status = status;
        this.processedAt = processedAt;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public UUID getWorkOrderId() {
        return workOrderId;
    }

    public InboxStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
