package com.mecanicadm.workorder_api.infra.messaging.config;

import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.AuthorizeWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.CancelWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.ChangesRequestedWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.ReleaseWorkOrderVehicleIntegrationCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RabbitMQConfigTest {

    private Message message(String eventType, String json) {
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        if (eventType != null) {
            properties.setHeader("event-type", eventType);
        }
        return new Message(json.getBytes(StandardCharsets.UTF_8), properties);
    }

    @Test
    @DisplayName("Deve converter o corpo no command indicado pelo header eventType, com workOrderId e campos proprios no mesmo nivel")
    void shouldConvertBodyToCommandNamedByEventTypeHeader() {
        UUID workOrderId = UUID.randomUUID();
        Message message = message("CancelWorkOrderIntegrationCommand", "{\"workOrderId\":\"" + workOrderId
                + "\",\"extra\":1}");

        Object converted = new RabbitMQConfig().commandMessageConverter().fromMessage(message);

        CancelWorkOrderIntegrationCommand command = assertInstanceOf(CancelWorkOrderIntegrationCommand.class, converted);
        assertEquals(workOrderId, command.workOrderId());
    }

    @Test
    @DisplayName("Deve converter cada um dos 4 integration commands pelo header eventType")
    void shouldConvertEveryIntegrationCommandByEventTypeHeader() {
        UUID id = UUID.randomUUID();
        RabbitMQConfig config = new RabbitMQConfig();
        String body = "{\"workOrderId\":\"" + id + "\"}";

        assertInstanceOf(AuthorizeWorkOrderIntegrationCommand.class,
                config.commandMessageConverter().fromMessage(message("AuthorizeWorkOrderIntegrationCommand", body)));
        assertInstanceOf(ReleaseWorkOrderVehicleIntegrationCommand.class,
                config.commandMessageConverter().fromMessage(message("ReleaseWorkOrderVehicleIntegrationCommand", body)));
        ChangesRequestedWorkOrderIntegrationCommand changes = assertInstanceOf(ChangesRequestedWorkOrderIntegrationCommand.class,
                config.commandMessageConverter().fromMessage(message("ChangesRequestedWorkOrderIntegrationCommand",
                        "{\"workOrderId\":\"" + id + "\",\"observation\":\"Nao trocar filtro\"}")));
        assertEquals("Nao trocar filtro", changes.observation());
        assertEquals(id, changes.toCommand().workOrderId());
        assertEquals("Nao trocar filtro", changes.toCommand().observation());
    }

    @Test
    @DisplayName("Deve recusar a conversao com MessageConversionException quando o eventType for desconhecido ou faltar")
    void shouldRefuseConversionWhenEventTypeIsUnknownOrMissing() {
        RabbitMQConfig config = new RabbitMQConfig();

        Jackson2JsonMessageConverter converter = config.commandMessageConverter();
        Message unknownType = message("DesconhecidoCommand", "{}");
        Message missingType = message(null, "{}");

        assertThrows(MessageConversionException.class, () -> converter.fromMessage(unknownType));
        assertThrows(MessageConversionException.class, () -> converter.fromMessage(missingType));
    }
}
