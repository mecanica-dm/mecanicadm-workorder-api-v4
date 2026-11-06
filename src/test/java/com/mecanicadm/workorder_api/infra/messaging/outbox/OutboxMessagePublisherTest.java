package com.mecanicadm.workorder_api.infra.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanicadm.workorder_api.infra.messaging.serialization.MessageJsonSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OutboxMessagePublisherTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OutboxJpaRepository repository = mock(OutboxJpaRepository.class);
    private final OutboxMessagePublisher publisher = new OutboxMessagePublisher(repository, new MessageJsonSerializer());

    @Test
    @DisplayName("Deve gravar na outbox a mensagem plana, sem messageId e eventType no corpo e com o nome da classe como tipo, para qualquer mensagem")
    void shouldStoreFlatMessageForAnyMessageWhenPublishing() throws Exception {
        WorkOrderSampleEvent message = new WorkOrderSampleEvent(UUID.randomUUID(), "ola");

        publisher.publish(message.workOrderId(), message);

        ArgumentCaptor<OutboxJpaEntity> captor = ArgumentCaptor.forClass(OutboxJpaEntity.class);
        verify(repository).save(captor.capture());
        OutboxJpaEntity saved = captor.getValue();
        JsonNode json = objectMapper.readTree(saved.getPayload());

        assertAll(
                () -> assertNotNull(saved.getId()),
                () -> assertEquals(message.workOrderId(), saved.getAggregateId()),
                () -> assertEquals("WorkOrderSampleEvent", saved.getType()),
                () -> assertFalse(json.has("messageId"), "messageId vai so no header"),
                () -> assertFalse(json.has("eventType"), "eventType vai so no header"),
                () -> assertEquals(message.workOrderId().toString(), json.get("workOrderId").asText()),
                () -> assertEquals("ola", json.get("note").asText()),
                () -> assertFalse(json.has("occurredAt"), "occurredAt vai so no header timestamp"),
                () -> assertNotNull(saved.getOccurredAt())
        );
    }
}
