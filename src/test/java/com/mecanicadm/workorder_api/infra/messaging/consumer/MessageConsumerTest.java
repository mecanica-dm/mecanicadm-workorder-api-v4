package com.mecanicadm.workorder_api.infra.messaging.consumer;

import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.CancelWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.messaging.inbox.InboxJpaRepository;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageConsumerTest {

    private static final long DELIVERY_TAG = 42L;

    private final Channel channel = mock(Channel.class);
    private final InboxJpaRepository inbox = mock(InboxJpaRepository.class);
    private final MessageConsumerSupport support = mock(MessageConsumerSupport.class);
    private final MessageConsumer consumer = new MessageConsumer(support) {
    };

    @BeforeEach
    void setUp() {
        when(support.transactionTemplate()).thenReturn(new TransactionTemplate(mock(PlatformTransactionManager.class)));
        when(support.inbox()).thenReturn(inbox);
        when(inbox.register(any(), anyString(), any(), anyString(), any())).thenReturn(1);
    }

    private Message amqpMessage(UUID messageId) {
        MessageProperties properties = new MessageProperties();
        properties.setDeliveryTag(DELIVERY_TAG);
        properties.setHeader("eventType", "CancelWorkOrderIntegrationCommand");
        if (messageId != null) {
            properties.setMessageId(messageId.toString());
        }
        return new Message(new byte[0], properties);
    }

    private CancelWorkOrderIntegrationCommand command() {
        return new CancelWorkOrderIntegrationCommand(UUID.randomUUID());
    }

    private void consume(CancelWorkOrderIntegrationCommand command, Consumer<CancelWorkOrderIntegrationCommand> handler) throws IOException {
        consume(command, UUID.randomUUID(), handler);
    }

    private void consume(CancelWorkOrderIntegrationCommand command, UUID messageId, Consumer<CancelWorkOrderIntegrationCommand> handler) throws IOException {
        consumer.consume(command.workOrderId(), command, amqpMessage(messageId), channel, handler);
    }

    private void verifyAckedOnly() throws IOException {
        verify(channel).basicAck(DELIVERY_TAG, false);
        verify(channel, never()).basicNack(any(Long.class), any(Boolean.class), any(Boolean.class));
    }

    @Test
    @DisplayName("Deve executar o handler e dar ack depois de processar a mensagem com sucesso")
    void shouldRunHandlerAndAckWhenMessageIsProcessed() throws IOException {
        AtomicInteger calls = new AtomicInteger();

        consume(command(), message -> calls.incrementAndGet());

        assertEquals(1, calls.get());
        verifyAckedOnly();
    }

    @Test
    @DisplayName("Deve usar o messageId enviado no header da mensagem para registrar no inbox")
    void shouldUseMessageIdFromHeaderWhenPresent() throws IOException {
        UUID messageId = UUID.randomUUID();
        CancelWorkOrderIntegrationCommand command = command();
        Message message = amqpMessage(null);
        message.getMessageProperties().setHeader("messageId", messageId.toString());

        consumer.consume(command.workOrderId(), command, message, channel, received -> { });

        verify(inbox).register(eq(messageId), eq("CancelWorkOrderIntegrationCommand"), eq(command.workOrderId()), anyString(), any());
        verifyAckedOnly();
    }

    @Test
    @DisplayName("Deve dar ack sem executar o handler quando a mensagem for duplicata")
    void shouldAckWithoutRunningHandlerWhenMessageIsDuplicate() throws IOException {
        when(inbox.register(any(), anyString(), any(), anyString(), any())).thenReturn(0);
        AtomicInteger calls = new AtomicInteger();

        consume(command(), message -> calls.incrementAndGet());

        assertEquals(0, calls.get());
        verifyAckedOnly();
    }

    @Test
    @DisplayName("Deve dar ack e registrar REJECTED no inbox quando a regra de negocio recusar a mensagem")
    void shouldAckAndRegisterRejectedWhenBusinessRuleFails() throws IOException {
        CancelWorkOrderIntegrationCommand command = command();
        UUID messageId = UUID.randomUUID();

        consume(command, messageId, message -> {
            throw new WorkOrderExceptions.InvalidStatusTransition("IN_EXECUTION", "CANCELLED");
        });

        verify(inbox, times(2)).register(eq(messageId), eq("CancelWorkOrderIntegrationCommand"), eq(command.workOrderId()), anyString(), any());
        verify(inbox).register(eq(messageId), anyString(), any(), eq("REJECTED"), any());
        verifyAckedOnly();
    }

    @Test
    @DisplayName("Deve registrar FAILED no inbox e mandar para a DLQ (nack sem requeue) na primeira falha tecnica, sem nova tentativa")
    void shouldRegisterFailedAndNackWithoutRetryWhenTechnicalFailureHappens() throws IOException {
        AtomicInteger calls = new AtomicInteger();
        CancelWorkOrderIntegrationCommand command = command();
        UUID messageId = UUID.randomUUID();

        consume(command, messageId, message -> {
            calls.incrementAndGet();
            throw new IllegalStateException("banco fora");
        });

        assertEquals(1, calls.get());
        verify(inbox).registerFailure(eq(messageId), eq("CancelWorkOrderIntegrationCommand"), eq(command.workOrderId()), eq("banco fora"), any());
        verify(channel).basicNack(DELIVERY_TAG, false, false);
        verify(channel, never()).basicAck(any(Long.class), any(Boolean.class));
    }

    @Test
    @DisplayName("Deve mandar para a DLQ, sem executar o handler nem registrar no inbox, quando a mensagem vier sem messageId (header) ou workOrderId")
    void shouldNackWithoutRunningHandlerWhenHeaderIsIncomplete() throws IOException {
        AtomicInteger calls = new AtomicInteger();
        CancelWorkOrderIntegrationCommand incomplete = new CancelWorkOrderIntegrationCommand(null);

        consume(incomplete, null, message -> calls.incrementAndGet());

        assertEquals(0, calls.get());
        verify(inbox, never()).register(any(), anyString(), any(), anyString(), any());
        verify(inbox, never()).registerFailure(any(), anyString(), any(), any(), any());
        verify(channel, times(1)).basicNack(DELIVERY_TAG, false, false);
    }
}
