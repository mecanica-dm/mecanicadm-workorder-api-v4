package com.mecanicadm.workorder_api.infra.messaging.consumer;

import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import com.mecanicadm.workorder_api.core.workorder.usecase.AuthorizeWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.CancelWorkOrderUseCase;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.AuthorizeWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.CancelWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.WorkOrderConsumer;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Sql("/sql/saga_messaging_test_data.sql")
class CommandProcessingIT extends AbstractIntegrationTest {
    private static final UUID RECEIVED = UUID.fromString("770e8400-e29b-41d4-a716-446655440100");
    private static final UUID IN_EXECUTION = UUID.fromString("770e8400-e29b-41d4-a716-446655440101");
    private static final UUID DIAGNOSED = UUID.fromString("770e8400-e29b-41d4-a716-446655440102");

    @Autowired
    private WorkOrderConsumer consumer;

    @Autowired
    private CancelWorkOrderUseCase cancelWorkOrderUseCase;

    @Autowired
    private AuthorizeWorkOrderUseCase authorizeWorkOrderUseCase;

    @Autowired
    private JdbcTemplate jdbc;

    private final Map<Object, UUID> messageIds = new IdentityHashMap<>();

    private CancelWorkOrderIntegrationCommand cancel(UUID messageId, UUID workOrderId) {
        CancelWorkOrderIntegrationCommand command = new CancelWorkOrderIntegrationCommand(workOrderId);
        messageIds.put(command, messageId);
        return command;
    }

    private AuthorizeWorkOrderIntegrationCommand authorize(UUID workOrderId) {
        AuthorizeWorkOrderIntegrationCommand command = new AuthorizeWorkOrderIntegrationCommand(workOrderId);
        messageIds.put(command, UUID.randomUUID());
        return command;
    }

    private ProcessingResult process(CancelWorkOrderIntegrationCommand command, Consumer<CancelWorkOrderIntegrationCommand> handler) {
        return consumer.process(envelope(command.workOrderId(), command), command, handler);
    }

    private ProcessingResult process(AuthorizeWorkOrderIntegrationCommand command, Consumer<AuthorizeWorkOrderIntegrationCommand> handler) {
        return consumer.process(envelope(command.workOrderId(), command), command, handler);
    }

    private MessageConsumer.Envelope envelope(UUID workOrderId, Object command) {
        return new MessageConsumer.Envelope(messageIds.get(command), command.getClass().getSimpleName(), workOrderId);
    }

    private int status(UUID workOrderId) {
        return jdbc.queryForObject("SELECT status FROM work_orders WHERE id = ?", Integer.class, workOrderId);
    }

    private List<Map<String, Object>> outbox(UUID workOrderId) {
        return jdbc.queryForList("SELECT type, payload::text AS payload FROM outbox WHERE aggregate_id = ? ORDER BY sequence", workOrderId);
    }

    private String inboxStatus(UUID messageId) {
        return jdbc.queryForObject("SELECT status FROM inbox WHERE message_id = ?", String.class, messageId);
    }

    private int inboxCount(UUID messageId) {
        return jdbc.queryForObject("SELECT count(*) FROM inbox WHERE message_id = ?", Integer.class, messageId);
    }

    @Test
    @DisplayName("Deve aplicar o command, gravar o evento na outbox e registrar no inbox na mesma transacao")
    void shouldApplyCommandAndWriteOutboxAndInboxTogether() {
        UUID messageId = UUID.randomUUID();

        ProcessingResult result = process(cancel(messageId, RECEIVED), received -> cancelWorkOrderUseCase.execute(received.toCommand()));

        List<Map<String, Object>> events = outbox(RECEIVED);
        assertEquals(ProcessingResult.PROCESSED, result);
        assertEquals(8, status(RECEIVED));
        assertEquals(1, events.size());
        assertEquals("WorkOrderCanceledEvent", events.get(0).get("type"));
        assertEquals(1, inboxCount(messageId));
        assertEquals("PROCESSED", inboxStatus(messageId));
        assertTrue(((String) events.get(0).get("payload")).contains(RECEIVED.toString()));
    }

    @Test
    @DisplayName("Nao deve executar duas vezes o command duplicado (mesmo messageId)")
    void shouldNotExecuteDuplicateCommandTwice() {
        UUID messageId = UUID.randomUUID();
        CancelWorkOrderIntegrationCommand command = cancel(messageId, RECEIVED);

        ProcessingResult first = process(command, received -> cancelWorkOrderUseCase.execute(received.toCommand()));
        ProcessingResult second = process(command, received -> cancelWorkOrderUseCase.execute(received.toCommand()));

        assertEquals(ProcessingResult.PROCESSED, first);
        assertEquals(ProcessingResult.DUPLICATE, second);
        assertEquals(1, outbox(RECEIVED).size());
        assertEquals(1, inboxCount(messageId));
    }

    @Test
    @DisplayName("Deve registrar REJECTED no inbox, manter o estado e nao gerar evento quando a regra de negocio recusar o command")
    void shouldRegisterRejectedWhenBusinessRuleFails() {
        UUID messageId = UUID.randomUUID();

        ProcessingResult result = process(cancel(messageId, IN_EXECUTION), received -> cancelWorkOrderUseCase.execute(received.toCommand()));

        assertEquals(ProcessingResult.REJECTED, result);
        assertEquals(0, status(IN_EXECUTION));
        assertEquals(0, outbox(IN_EXECUTION).size());
        assertEquals("REJECTED", inboxStatus(messageId));
    }

    @Test
    @DisplayName("Deve reprocessar o mesmo messageId recusado quando for reenviado e a regra de negocio passar a aceitar")
    void shouldReprocessRejectedMessageWhenRedelivered() {
        UUID messageId = UUID.randomUUID();

        ProcessingResult first = process(cancel(messageId, IN_EXECUTION), received -> cancelWorkOrderUseCase.execute(received.toCommand()));
        jdbc.update("UPDATE work_orders SET status = 3 WHERE id = ?", IN_EXECUTION);
        ProcessingResult second = process(cancel(messageId, IN_EXECUTION), received -> cancelWorkOrderUseCase.execute(received.toCommand()));

        assertEquals(ProcessingResult.REJECTED, first);
        assertEquals(ProcessingResult.PROCESSED, second);
        assertEquals(8, status(IN_EXECUTION));
        assertEquals(1, inboxCount(messageId));
        assertEquals("PROCESSED", inboxStatus(messageId));
        assertEquals(1, outbox(IN_EXECUTION).size());
    }

    @Test
    @DisplayName("Deve reprocessar o mesmo messageId recusado por OS inexistente quando reenviado com a OS correta")
    void shouldReprocessMessageRejectedForUnknownWorkOrderWhenRedeliveredWithValidWorkOrder() {
        UUID messageId = UUID.randomUUID();

        ProcessingResult first = process(cancel(messageId, UUID.randomUUID()), received -> cancelWorkOrderUseCase.execute(received.toCommand()));
        ProcessingResult second = process(cancel(messageId, RECEIVED), received -> cancelWorkOrderUseCase.execute(received.toCommand()));

        assertEquals(ProcessingResult.REJECTED, first);
        assertEquals(ProcessingResult.PROCESSED, second);
        assertEquals(8, status(RECEIVED));
        assertEquals("PROCESSED", inboxStatus(messageId));
    }

    @Test
    @DisplayName("Nao deve processar de novo um messageId ja processado")
    void shouldNotProcessAgainWhenMessageWasAlreadyProcessed() {
        CancelWorkOrderIntegrationCommand command = cancel(UUID.randomUUID(), RECEIVED);

        process(command, received -> cancelWorkOrderUseCase.execute(received.toCommand()));
        ProcessingResult second = process(command, received -> cancelWorkOrderUseCase.execute(received.toCommand()));

        assertEquals(ProcessingResult.DUPLICATE, second);
        assertEquals(1, outbox(RECEIVED).size());
    }

    @Test
    @DisplayName("Nao deve deixar efeito parcial nem registro no inbox quando ocorrer falha tecnica")
    void shouldLeaveNoTraceWhenTechnicalFailureHappensAfterEffect() {
        UUID messageId = UUID.randomUUID();

        CancelWorkOrderIntegrationCommand cancelCommand = cancel(messageId, RECEIVED);

        assertThrows(IllegalStateException.class, () -> process(cancelCommand, command -> {
            cancelWorkOrderUseCase.execute(command.toCommand());
            throw new IllegalStateException("falha tecnica simulada");
        }));

        assertEquals(3, status(RECEIVED));
        assertEquals(0, outbox(RECEIVED).size());
        assertEquals(0, inboxCount(messageId));
    }

    @Test
    @DisplayName("Deve registrar FAILED no inbox, desfazer o efeito e mandar para a DLQ na falha tecnica, e reprocessar se for reenviada")
    void shouldRegisterFailedAndAllowReprocessingWhenTechnicalFailureHappens() throws Exception {
        CancelWorkOrderIntegrationCommand command = cancel(UUID.randomUUID(), RECEIVED);
        Channel channel = mock(Channel.class);

        consumer.consume(command.workOrderId(), command, amqpMessage(messageIds.get(command)), channel, message -> {
            cancelWorkOrderUseCase.execute(message.toCommand());
            throw new IllegalStateException("falha tecnica simulada");
        });

        assertEquals(3, status(RECEIVED));
        assertEquals(0, outbox(RECEIVED).size());
        assertEquals("FAILED", inboxStatus(messageIds.get(command)));
        verify(channel).basicNack(7L, false, false);

        consumer.consume(command.workOrderId(), command, amqpMessage(messageIds.get(command)), channel, received -> cancelWorkOrderUseCase.execute(received.toCommand()));

        assertEquals(8, status(RECEIVED));
        assertEquals("PROCESSED", inboxStatus(messageIds.get(command)));
        verify(channel).basicAck(7L, false);
    }

    private Message amqpMessage(UUID messageId) {
        MessageProperties properties = new MessageProperties();
        properties.setDeliveryTag(7L);
        properties.setMessageId(messageId.toString());
        properties.setHeader("event-type", "CancelWorkOrderIntegrationCommand");
        return new Message(new byte[0], properties);
    }

    @Test
    @DisplayName("Deve emitir os eventos de uma OS na ordem em que os commands foram processados")
    void shouldEmitEventsInProcessingOrder() {
        process(authorize(DIAGNOSED), received -> authorizeWorkOrderUseCase.execute(received.toCommand()));
        process(cancel(UUID.randomUUID(), DIAGNOSED), received -> cancelWorkOrderUseCase.execute(received.toCommand()));

        List<Map<String, Object>> events = outbox(DIAGNOSED);
        assertEquals(2, events.size());
        assertEquals("WorkOrderAuthorizedEvent", events.get(0).get("type"));
        assertEquals("WorkOrderCanceledEvent", events.get(1).get("type"));
    }

    @Test
    @DisplayName("Deve registrar no inbox e nao gerar evento quando a OS do command nao existe")
    void shouldRegisterInboxAndEmitNothingWhenWorkOrderDoesNotExist() {
        UUID unknown = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        ProcessingResult result = process(cancel(messageId, unknown), received -> cancelWorkOrderUseCase.execute(received.toCommand()));

        assertEquals(ProcessingResult.REJECTED, result);
        assertEquals(0, outbox(unknown).size());
        assertEquals(1, inboxCount(messageId));
    }
}
