package com.mecanicadm.workorder_api.infra.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.infra.messaging.serialization.MessageJsonSerializer;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class OutboxMessagePublisher implements MessagePublisher {
    private final OutboxJpaRepository repository;
    private final MessageJsonSerializer serializer;

    public OutboxMessagePublisher(OutboxJpaRepository repository, MessageJsonSerializer serializer) {
        this.repository = repository;
        this.serializer = serializer;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(UUID workOrderId, Object message) {
        String eventType = message.getClass().getSimpleName();
        repository.save(new OutboxJpaEntity(
                UUID.randomUUID(),
                workOrderId,
                eventType,
                serialize(message, eventType),
                Instant.now()));
    }

    private String serialize(Object message, String eventType) {
        try {
            return serializer.toJson(message);
        } catch (JsonProcessingException e) {
            throw new TechnicalException("error.technical.outbox.serialization", eventType);
        }
    }
}
