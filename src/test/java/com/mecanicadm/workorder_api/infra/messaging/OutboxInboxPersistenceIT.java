package com.mecanicadm.workorder_api.infra.messaging;

import com.mecanicadm.workorder_api.infra.messaging.inbox.InboxJpaEntity;
import com.mecanicadm.workorder_api.infra.messaging.inbox.InboxJpaRepository;
import com.mecanicadm.workorder_api.infra.messaging.inbox.InboxStatus;
import com.mecanicadm.workorder_api.infra.messaging.outbox.OutboxJpaEntity;
import com.mecanicadm.workorder_api.infra.messaging.outbox.OutboxJpaRepository;
import com.mecanicadm.workorder_api.infra.messaging.outbox.OutboxStatus;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class OutboxInboxPersistenceIT extends AbstractIntegrationTest {

    private static final String PAYLOAD = "{\"messageId\":\"x\",\"a\":1}";

    @Autowired
    private OutboxJpaRepository outboxRepository;

    @Autowired
    private InboxJpaRepository inboxRepository;

    private OutboxJpaEntity outbox(UUID aggregateId) {
        return new OutboxJpaEntity(UUID.randomUUID(), aggregateId, "WorkOrderCreatedEvent",
                PAYLOAD, Instant.now().truncatedTo(ChronoUnit.MICROS));
    }

    @Test
    @DisplayName("Deve gravar outbox com sequence gerada pelo banco e payload jsonb")
    void shouldPersistOutboxWithGeneratedSequenceWhenSaving() {
        OutboxJpaEntity saved = outboxRepository.saveAndFlush(outbox(UUID.randomUUID()));

        assertNotNull(saved.getSequence());
        assertEquals(OutboxStatus.PENDING, saved.getStatus());
    }

    @Test
    @DisplayName("Deve travar apenas pendentes e na ordem global de sequence")
    void shouldLockOnlyPendingInSequenceOrderWhenLockingBatch() {
        UUID aggregate = UUID.randomUUID();
        OutboxJpaEntity first = outboxRepository.saveAndFlush(outbox(aggregate));
        OutboxJpaEntity second = outboxRepository.saveAndFlush(outbox(aggregate));
        OutboxJpaEntity published = outboxRepository.saveAndFlush(outbox(UUID.randomUUID()));
        published.markPublished(Instant.now());
        outboxRepository.saveAndFlush(published);

        List<OutboxJpaEntity> batch = outboxRepository.lockPendingBatch(10);

        List<UUID> ids = batch.stream().map(OutboxJpaEntity::getId).toList();
        assertEquals(List.of(first.getId(), second.getId()), ids.stream().filter(id -> id.equals(first.getId()) || id.equals(second.getId())).toList());
        assertFalse(ids.contains(published.getId()));
    }

    @Test
    @DisplayName("Deve apagar apenas mensagens publicadas antes do limite")
    void shouldDeleteOnlyPublishedBeforeThresholdWhenCleaning() {
        Instant now = Instant.now();
        OutboxJpaEntity old = outbox(UUID.randomUUID());
        old.markPublished(now.minus(10, ChronoUnit.DAYS));
        OutboxJpaEntity recent = outbox(UUID.randomUUID());
        recent.markPublished(now);
        OutboxJpaEntity pending = outbox(UUID.randomUUID());
        outboxRepository.saveAllAndFlush(List.of(old, recent, pending));

        int deleted = outboxRepository.deletePublishedBefore(now.minus(7, ChronoUnit.DAYS));

        assertEquals(1, deleted);
        assertFalse(outboxRepository.existsById(old.getId()));
        assertTrue(outboxRepository.existsById(recent.getId()));
        assertTrue(outboxRepository.existsById(pending.getId()));
    }

    @Test
    @DisplayName("Deve registrar a mensagem na primeira vez e ignorar a duplicata no inbox")
    void shouldIgnoreDuplicateMessageWhenRegisteringTwiceInInbox() {
        UUID messageId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();

        int first = inboxRepository.register(messageId, "CancelWorkOrderIntegrationCommand", workOrderId, "PROCESSED", Instant.now());
        int second = inboxRepository.register(messageId, "CancelWorkOrderIntegrationCommand", workOrderId, "PROCESSED", Instant.now());

        assertEquals(1, first);
        assertEquals(0, second);
    }

    @Test
    @DisplayName("Deve registrar a falha no inbox com status FAILED e o motivo")
    void shouldRegisterFailureWithStatusFailedAndError() {
        UUID messageId = UUID.randomUUID();

        inboxRepository.registerFailure(messageId, "CancelWorkOrderIntegrationCommand", UUID.randomUUID(), "banco fora", Instant.now());

        InboxJpaEntity stored = inboxRepository.findById(messageId).orElseThrow();
        assertEquals(InboxStatus.FAILED, stored.getStatus());
        assertEquals("banco fora", stored.getError());
    }

    @Test
    @DisplayName("Deve permitir reprocessar uma mensagem FAILED reenviada e passar para PROCESSED")
    void shouldAllowReprocessingFailedMessageWhenRedelivered() {
        UUID messageId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        inboxRepository.registerFailure(messageId, "CancelWorkOrderIntegrationCommand", workOrderId, "banco fora", Instant.now());

        int reprocessed = inboxRepository.register(messageId, "CancelWorkOrderIntegrationCommand", workOrderId, "PROCESSED", Instant.now());

        assertEquals(1, reprocessed);
        inboxRepository.flush();
        assertEquals(InboxStatus.PROCESSED, inboxRepository.findById(messageId).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("Deve permitir reprocessar uma mensagem REJECTED reenviada, inclusive com outra OS, e passar para PROCESSED")
    void shouldAllowReprocessingRejectedMessageWhenRedelivered() {
        UUID messageId = UUID.randomUUID();
        UUID otherWorkOrderId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        inboxRepository.register(messageId, "CancelWorkOrderIntegrationCommand", otherWorkOrderId, "REJECTED", Instant.now());

        int reprocessed = inboxRepository.register(messageId, "CancelWorkOrderIntegrationCommand", workOrderId, "PROCESSED", Instant.now());

        assertEquals(1, reprocessed);
        inboxRepository.flush();
        InboxJpaEntity stored = inboxRepository.findById(messageId).orElseThrow();
        assertEquals(InboxStatus.PROCESSED, stored.getStatus());
        assertEquals(workOrderId, stored.getWorkOrderId());
    }

    @Test
    @DisplayName("Nao deve reprocessar uma mensagem PROCESSED reenviada")
    void shouldNotReprocessProcessedMessageWhenRedelivered() {
        UUID messageId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        inboxRepository.register(messageId, "CancelWorkOrderIntegrationCommand", workOrderId, "PROCESSED", Instant.now());

        int again = inboxRepository.register(messageId, "CancelWorkOrderIntegrationCommand", workOrderId, "PROCESSED", Instant.now());

        assertEquals(0, again);
    }
}
