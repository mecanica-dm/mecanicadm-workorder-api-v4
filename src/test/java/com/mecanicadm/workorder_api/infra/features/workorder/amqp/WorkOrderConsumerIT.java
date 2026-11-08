package com.mecanicadm.workorder_api.infra.features.workorder.amqp;

import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Sql("/sql/saga_messaging_test_data.sql")
class WorkOrderConsumerIT extends AbstractIntegrationTest {

    private static final UUID RECEIVED = UUID.fromString("770e8400-e29b-41d4-a716-446655440100");
    private static final UUID IN_EXECUTION = UUID.fromString("770e8400-e29b-41d4-a716-446655440101");
    private static final UUID DIAGNOSED = UUID.fromString("770e8400-e29b-41d4-a716-446655440102");

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @BeforeAll
    static void applyTopology() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://" + rabbit.getHost() + ":" + rabbit.getHttpPort() + "/api/definitions"))
                .header("content-type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                        (rabbit.getAdminUsername() + ":" + rabbit.getAdminPassword()).getBytes(StandardCharsets.UTF_8)))
                .POST(HttpRequest.BodyPublishers.ofFile(Path.of("messaging/workorder-definitions.json")))
                .build();
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        AtomicReference<HttpResponse<String>> response = new AtomicReference<>();
        await().atMost(Duration.ofSeconds(60)).ignoreExceptions().until(() -> {
            response.set(client.send(request, HttpResponse.BodyHandlers.ofString()));
            return true;
        });
        assertTrue(response.get().statusCode() < 300, "Falha ao aplicar a topologia: " + response.get().body());
    }

    @DynamicPropertySource
    static void configureRabbit(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void waitForQueue() {
        await().atMost(Duration.ofSeconds(30)).until(() -> amqpAdmin.getQueueProperties("workorder") != null);
        rabbitTemplate.execute(channel -> channel.queuePurge("workorder.dlq"));
    }

    private void send(String eventType, String body) {
        send("workorder-input", eventType, body);
    }

    private void send(String exchange, String eventType, String body) {
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        properties.setMessageId(UUID.randomUUID().toString());
        if (eventType != null) {
            properties.setHeader("event-type", eventType);
        }
        Message message = MessageBuilder.withBody(body.getBytes(StandardCharsets.UTF_8)).andProperties(properties).build();
        rabbitTemplate.send(exchange, "", message);
    }

    private String command(UUID workOrderId, String extra) {
        return "{\"workOrderId\":\"" + workOrderId + "\"" + extra + "}";
    }

    private int status(UUID workOrderId) {
        return jdbc.queryForObject("SELECT status FROM work_orders WHERE id = ?", Integer.class, workOrderId);
    }

    private List<String> inboxStatuses(UUID workOrderId) {
        return jdbc.queryForList("SELECT status FROM inbox WHERE work_order_id = ?", String.class, workOrderId);
    }

    private List<String> outboxTypes(UUID workOrderId) {
        return jdbc.queryForList("SELECT type FROM outbox WHERE aggregate_id = ? ORDER BY sequence", String.class, workOrderId);
    }

    @Test
    @DisplayName("Deve cancelar a OS e gravar o evento quando chegar CancelWorkOrderIntegrationCommand pela exchange workorder-input")
    void shouldCancelWorkOrderWhenCancelCommandArrives() {
        send("CancelWorkOrderIntegrationCommand", command(RECEIVED, ""));

        await().atMost(Duration.ofSeconds(15)).until(() -> status(RECEIVED) == 8);
        assertEquals(List.of("WorkOrderCanceledEvent"), outboxTypes(RECEIVED));
    }

    @Test
    @DisplayName("Deve receber pela saga-output: o workorder-input escuta a saga-output e repassa ao consumer")
    void shouldReceiveCommandPublishedToSagaOutput() {
        send("saga-output", "CancelWorkOrderIntegrationCommand", command(RECEIVED, ""));

        await().atMost(Duration.ofSeconds(15)).until(() -> status(RECEIVED) == 8);
        assertEquals(List.of("WorkOrderCanceledEvent"), outboxTypes(RECEIVED));
    }

    @Test
    @DisplayName("Deve escolher o metodo pelo eventType: AuthorizeWorkOrderIntegrationCommand autoriza a OS diagnosticada")
    void shouldRouteAuthorizeCommandToAuthorizeHandler() {
        send("AuthorizeWorkOrderIntegrationCommand", command(DIAGNOSED, ""));

        await().atMost(Duration.ofSeconds(15)).until(() -> status(DIAGNOSED) == 1);
        assertEquals(List.of("WorkOrderAuthorizedEvent"), outboxTypes(DIAGNOSED));
    }

    @Test
    @DisplayName("Deve registrar a solicitacao de mudancas quando chegar ChangesRequestedWorkOrderIntegrationCommand")
    void shouldRouteChangesRequestedCommandToRequestChangesHandler() {
        send("ChangesRequestedWorkOrderIntegrationCommand",
                command(DIAGNOSED, ",\"observation\":\"Nao trocar filtro do ar condicionado\""));

        await().atMost(Duration.ofSeconds(15)).until(() -> status(DIAGNOSED) == 7);
        assertEquals(List.of("WorkOrderChangesRequestedEvent"), outboxTypes(DIAGNOSED));
        assertEquals("Nao trocar filtro do ar condicionado",
                jdbc.queryForObject("SELECT requested_changes FROM work_orders WHERE id = ?", String.class, DIAGNOSED));
    }

    @Test
    @DisplayName("Deve registrar REJECTED no inbox, manter o estado e nao gerar evento quando a regra de negocio recusar")
    void shouldRegisterRejectedWhenBusinessRuleRefusesCommand() {
        send("CancelWorkOrderIntegrationCommand", command(IN_EXECUTION, ""));

        await().atMost(Duration.ofSeconds(15)).until(() -> inboxStatuses(IN_EXECUTION).equals(List.of("REJECTED")));
        assertEquals(0, status(IN_EXECUTION));
        assertEquals(List.of(), outboxTypes(IN_EXECUTION));
    }

    @Test
    @DisplayName("Deve mandar para a DLQ a mensagem com eventType desconhecido")
    void shouldSendToDeadLetterQueueWhenEventTypeIsUnknown() {
        amqpAdmin.declareBinding(new org.springframework.amqp.core.Binding("workorder", org.springframework.amqp.core.Binding.DestinationType.QUEUE,
                "workorder-input", "", java.util.Map.of("x-match", "all", "event-type", "DesconhecidoCommand")));

        send("DesconhecidoCommand", "{}");

        assertNotNull(rabbitTemplate.receive("workorder.dlq", 15000));
    }

    @Test
    @DisplayName("Deve mandar para a DLQ a mensagem com corpo ilegivel")
    void shouldSendToDeadLetterQueueWhenBodyIsNotReadable() {
        send("CancelWorkOrderIntegrationCommand", "isto nao e json");

        assertNotNull(rabbitTemplate.receive("workorder.dlq", 15000));
    }
}
