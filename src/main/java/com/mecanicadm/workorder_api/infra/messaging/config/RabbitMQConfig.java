package com.mecanicadm.workorder_api.infra.messaging.config;

import com.fasterxml.jackson.databind.JavaType;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.AuthorizeWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.CancelWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.ChangesRequestedWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.ReleaseWorkOrderVehicleIntegrationCommand;
import com.mecanicadm.workorder_api.infra.messaging.outbox.RabbitOutboxMessageSender;
import com.mecanicadm.workorder_api.infra.messaging.serialization.MessageJsonSerializer;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class RabbitMQConfig {
    private static final List<Class<?>> COMMANDS = List.of(CancelWorkOrderIntegrationCommand.class,
            AuthorizeWorkOrderIntegrationCommand.class, ChangesRequestedWorkOrderIntegrationCommand.class,
            ReleaseWorkOrderVehicleIntegrationCommand.class);

    @Bean
    public Jackson2JsonMessageConverter commandMessageConverter() {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper() {
            @Override
            public String getClassIdFieldName() {
                return RabbitOutboxMessageSender.EVENT_TYPE_HEADER;
            }

            @Override
            public JavaType toJavaType(MessageProperties properties) {
                Object eventType = properties.getHeaders().get(getClassIdFieldName());
                if (eventType == null || !getIdClassMapping().containsKey(eventType.toString())) {
                    throw new MessageConversionException("eventType desconhecido: " + eventType);
                }
                return super.toJavaType(properties);
            }
        };
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.setIdClassMapping(COMMANDS.stream().collect(Collectors.toMap(Class::getSimpleName, Function.identity())));

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(MessageJsonSerializer.newObjectMapper());
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory commandListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("commandMessageConverter") MessageConverter commandMessageConverter,
            @Value("${messaging.consumer.prefetch:10}") int prefetch) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(commandMessageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(prefetch);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
