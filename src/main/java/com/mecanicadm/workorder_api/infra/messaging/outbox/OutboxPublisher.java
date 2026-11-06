package com.mecanicadm.workorder_api.infra.messaging.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxJpaRepository repository;
    private final RabbitOutboxMessageSender sender;
    private final OutboxProperties properties;

    public OutboxPublisher(OutboxJpaRepository repository, RabbitOutboxMessageSender sender, OutboxProperties properties) {
        this.repository = repository;
        this.sender = sender;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${messaging.outbox.publisher.interval-ms:1000}")
    @Transactional
    public void publishPending() {
        for (OutboxJpaEntity message : repository.lockPendingBatch(properties.batchSize())) {
            try {
                sender.send(message);
                message.markPublished(Instant.now());
            } catch (RuntimeException e) {
                message.markFailed(e.getMessage());
                log.error("Falha ao publicar outbox id={} type={} workOrderId={}; status=FAILED",
                        message.getId(), message.getType(), message.getAggregateId(), e);
            }
        }
    }

    @Scheduled(cron = "${messaging.outbox.cleanup.cron:0 0 3 * * *}")
    @Transactional
    public int cleanPublished() {
        int deleted = repository.deletePublishedBefore(Instant.now().minus(Duration.ofDays(properties.retentionDays())));
        if (deleted > 0) {
            log.info("Outbox: {} mensagens publicadas removidas", deleted);
        }
        return deleted;
    }
}
