package com.mecanicadm.workorder_api.infra.messaging.outbox;

import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class OutboxPublisherIT extends AbstractIntegrationTest {
    @Autowired
    private OutboxPublisher publisher;

    @Autowired
    private OutboxJpaRepository repository;

    @MockitoBean
    private RabbitOutboxMessageSender sender;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    private OutboxJpaEntity pending(UUID aggregateId) {
        return repository.saveAndFlush(new OutboxJpaEntity(UUID.randomUUID(), aggregateId, "WorkOrderSampleEvent",
                "{\"k\":\"v\"}", Instant.now().truncatedTo(ChronoUnit.MICROS)));
    }

    @Test
    @DisplayName("Deve marcar published_at somente depois do envio confirmado")
    void shouldMarkPublishedOnlyAfterSendSucceeds() {
        OutboxJpaEntity message = pending(UUID.randomUUID());
        doAnswer(invocation -> {
            assertNull(repository.findById(message.getId()).orElseThrow().getPublishedAt());
            return null;
        }).when(sender).send(any());

        publisher.publishPending();

        OutboxJpaEntity stored = repository.findById(message.getId()).orElseThrow();
        assertNotNull(stored.getPublishedAt());
        assertEquals(OutboxStatus.PUBLISHED, stored.getStatus());
    }

    @Test
    @DisplayName("Deve marcar como FAILED e gravar o erro quando o envio falhar, sem nova tentativa")
    void shouldMarkFailedAndRecordErrorWhenSendFails() {
        OutboxJpaEntity message = pending(UUID.randomUUID());
        doThrow(new IllegalStateException("broker fora do ar")).when(sender).send(any());

        publisher.publishPending();

        OutboxJpaEntity stored = repository.findById(message.getId()).orElseThrow();
        assertNull(stored.getPublishedAt());
        assertEquals(OutboxStatus.FAILED, stored.getStatus());
        assertEquals("broker fora do ar", stored.getLastError());

        publisher.publishPending();
        verify(sender, times(1)).send(any());
    }

    @Test
    @DisplayName("Deve marcar apenas a mensagem que falhou como FAILED e seguir com as demais do lote")
    void shouldMarkOnlyFailingMessageAsFailedAndContinueBatch() {
        OutboxJpaEntity first = pending(UUID.randomUUID());
        OutboxJpaEntity failing = pending(UUID.randomUUID());
        OutboxJpaEntity afterFailure = pending(UUID.randomUUID());
        doThrow(new IllegalStateException("nack")).when(sender)
                .send(ArgumentMatchers.argThat(m -> m.getId().equals(failing.getId())));

        publisher.publishPending();

        assertEquals(OutboxStatus.PUBLISHED, repository.findById(first.getId()).orElseThrow().getStatus());
        assertEquals(OutboxStatus.FAILED, repository.findById(failing.getId()).orElseThrow().getStatus());
        assertEquals(OutboxStatus.PUBLISHED, repository.findById(afterFailure.getId()).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("Deve publicar na ordem de sequence")
    void shouldPublishInSequenceOrder() {
        UUID aggregate = UUID.randomUUID();
        OutboxJpaEntity first = pending(aggregate);
        OutboxJpaEntity second = pending(aggregate);
        OutboxJpaEntity third = pending(UUID.randomUUID());

        publisher.publishPending();

        ArgumentCaptor<OutboxJpaEntity> captor = ArgumentCaptor.forClass(OutboxJpaEntity.class);
        verify(sender, times(3)).send(captor.capture());
        assertEquals(List.of(first.getId(), second.getId(), third.getId()),
                captor.getAllValues().stream().map(OutboxJpaEntity::getId).toList());
    }

    @Test
    @DisplayName("Nao deve reenviar mensagens ja publicadas")
    void shouldNotResendAlreadyPublishedMessages() {
        pending(UUID.randomUUID());

        publisher.publishPending();
        publisher.publishPending();

        verify(sender, times(1)).send(any());
    }

    @Test
    @DisplayName("Deve remover apenas publicadas antigas na limpeza, mantendo as FAILED")
    void shouldRemoveOnlyOldPublishedMessagesWhenCleaning() {
        OutboxJpaEntity old = pending(UUID.randomUUID());
        old.markPublished(Instant.now().minus(30, ChronoUnit.DAYS));
        repository.saveAndFlush(old);
        OutboxJpaEntity recent = pending(UUID.randomUUID());
        recent.markPublished(Instant.now());
        repository.saveAndFlush(recent);
        OutboxJpaEntity stillPending = pending(UUID.randomUUID());

        int deleted = publisher.cleanPublished();

        assertEquals(1, deleted);
        assertFalse(repository.existsById(old.getId()));
        assertTrue(repository.existsById(recent.getId()));
        assertTrue(repository.existsById(stillPending.getId()));
    }
}
