package com.mecanicadm.workorder_api.infra.messaging.outbox;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Testcontainers
class RabbitOutboxMessageSenderIT {
    private static final String EXCHANGE = "test.saga.exchange";
    private static final String QUEUE = "test.saga.queue";

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    private static CachingConnectionFactory connectionFactory;
    private static RabbitTemplate template;
    private static RabbitOutboxMessageSender sender;

    @BeforeAll
    static void setUp() {
        connectionFactory = new CachingConnectionFactory(rabbit.getHost(), rabbit.getAmqpPort());
        connectionFactory.setUsername(rabbit.getAdminUsername());
        connectionFactory.setPassword(rabbit.getAdminPassword());
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);

        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        HeadersExchange exchange = new HeadersExchange(EXCHANGE, true, false);
        Queue queue = new Queue(QUEUE, true);
        admin.declareExchange(exchange);
        admin.declareQueue(queue);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).whereAll(Map.of("event-type", "WorkOrderSampleEvent")).match());

        template = new RabbitTemplate(connectionFactory);
        sender = new RabbitOutboxMessageSender(template, EXCHANGE, new OutboxProperties(50, 5000, 7));
    }

    @AfterAll
    static void tearDown() {
        connectionFactory.destroy();
    }

    private OutboxJpaEntity outbox(String eventType) {
        return new OutboxJpaEntity(UUID.randomUUID(), UUID.randomUUID(), eventType,
                "{\"hello\":\"world\"}", Instant.now());
    }

    @Test
    @DisplayName("Deve publicar com confirm do broker e entregar a mensagem intacta com messageId e eventType somente nos headers")
    void shouldPublishWithBrokerConfirmWhenRouteExists() {
        OutboxJpaEntity message = outbox("WorkOrderSampleEvent");

        sender.send(message);

        Message received = template.receive(QUEUE, 5000);
        assertNotNull(received);
        assertEquals("{\"hello\":\"world\"}", new String(received.getBody(), StandardCharsets.UTF_8));
        assertNull(received.getMessageProperties().getMessageId());
        assertFalse(received.getMessageProperties().getHeaders().containsKey("spring_returned_message_correlation"));
        assertEquals("WorkOrderSampleEvent", received.getMessageProperties().getHeader("event-type"));
        assertEquals(message.getId().toString(), received.getMessageProperties().getHeader("message-id"));
        assertEquals("application/json", received.getMessageProperties().getContentType());
    }

    @Test
    @DisplayName("Nao deve falhar quando nenhuma fila assina o eventType")
    void shouldNotFailWhenNoQueueSubscribesToEventType() {
        OutboxJpaEntity message = outbox("EventNobodyListens");

        assertDoesNotThrow(() -> sender.send(message));
    }
}
