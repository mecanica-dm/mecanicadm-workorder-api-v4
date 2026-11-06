package com.mecanicadm.workorder_api.infra.features.workorder.amqp;

import com.mecanicadm.workorder_api.core.workorder.usecase.AuthorizeWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.CancelWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.ReleaseWorkOrderVehicleUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.RequestWorkOrderChangesUseCase;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.AuthorizeWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.CancelWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.ChangesRequestedWorkOrderIntegrationCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.amqp.command.ReleaseWorkOrderVehicleIntegrationCommand;
import com.mecanicadm.workorder_api.infra.messaging.consumer.MessageConsumer;
import com.mecanicadm.workorder_api.infra.messaging.consumer.MessageConsumerSupport;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "${messaging.queue.workorder:workorder}", containerFactory = "commandListenerContainerFactory")
public class WorkOrderConsumer extends MessageConsumer {

    private final CancelWorkOrderUseCase cancelUseCase;
    private final AuthorizeWorkOrderUseCase authorizeUseCase;
    private final RequestWorkOrderChangesUseCase requestChangesUseCase;
    private final ReleaseWorkOrderVehicleUseCase releaseVehicleUseCase;

    public WorkOrderConsumer(MessageConsumerSupport support,
                             CancelWorkOrderUseCase cancelUseCase,
                             AuthorizeWorkOrderUseCase authorizeUseCase,
                             RequestWorkOrderChangesUseCase requestChangesUseCase,
                             ReleaseWorkOrderVehicleUseCase releaseVehicleUseCase) {
        super(support);
        this.cancelUseCase = cancelUseCase;
        this.authorizeUseCase = authorizeUseCase;
        this.requestChangesUseCase = requestChangesUseCase;
        this.releaseVehicleUseCase = releaseVehicleUseCase;
    }

    @RabbitHandler
    public void onCancel(@Payload CancelWorkOrderIntegrationCommand command, Message message, Channel channel) throws IOException {
        consume(command.workOrderId(), command, message, channel, received -> cancelUseCase.execute(received.toCommand()));
    }

    @RabbitHandler
    public void onAuthorize(@Payload AuthorizeWorkOrderIntegrationCommand command, Message message, Channel channel) throws IOException {
        consume(command.workOrderId(), command, message, channel, received -> authorizeUseCase.execute(received.toCommand()));
    }

    @RabbitHandler
    public void onRequestChanges(@Payload ChangesRequestedWorkOrderIntegrationCommand command, Message message, Channel channel) throws IOException {
        consume(command.workOrderId(), command, message, channel, received -> requestChangesUseCase.execute(received.toCommand()));
    }

    @RabbitHandler
    public void onReleaseVehicle(@Payload ReleaseWorkOrderVehicleIntegrationCommand command, Message message, Channel channel) throws IOException {
        consume(command.workOrderId(), command, message, channel, received -> releaseVehicleUseCase.execute(received.toCommand()));
    }
}
