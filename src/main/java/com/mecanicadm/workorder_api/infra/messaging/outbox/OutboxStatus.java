package com.mecanicadm.workorder_api.infra.messaging.outbox;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
