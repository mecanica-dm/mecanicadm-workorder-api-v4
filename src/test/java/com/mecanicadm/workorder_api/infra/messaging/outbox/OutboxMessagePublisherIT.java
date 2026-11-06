package com.mecanicadm.workorder_api.infra.messaging.outbox;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OutboxMessagePublisherIT extends AbstractIntegrationTest {
    @Autowired
    private MessagePublisher publisher;

    @Autowired
    private OutboxJpaRepository repository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    private WorkOrderSampleEvent newMessage() {
        return WorkOrderSampleEvent.of("x");
    }

    @Test
    @DisplayName("Deve gravar a mensagem na outbox quando a transacao der commit")
    void shouldPersistMessageWhenTransactionCommits() {
        WorkOrderSampleEvent message = newMessage();

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> publisher.publish(message.workOrderId(), message));

        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("Nao deve deixar linha na outbox quando a transacao der rollback")
    void shouldLeaveNoRowWhenTransactionRollsBack() {
        WorkOrderSampleEvent message = newMessage();

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            publisher.publish(message.workOrderId(), message);
            status.setRollbackOnly();
        });

        assertEquals(0, repository.count());
    }

    @Test
    @DisplayName("Nao deve permitir publicar fora de uma transacao")
    void shouldRejectPublishWhenThereIsNoTransaction() {
        WorkOrderSampleEvent message = newMessage();

        assertThrows(IllegalTransactionStateException.class, () -> publisher.publish(message.workOrderId(), message));
        assertEquals(0, repository.count());
    }
}
