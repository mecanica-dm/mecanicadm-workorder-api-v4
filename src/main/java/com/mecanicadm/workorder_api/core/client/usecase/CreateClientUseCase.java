package com.mecanicadm.workorder_api.core.client.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.client.usecase.command.CreateClientCommand;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.UUID;

public class CreateClientUseCase implements UseCase<CreateClientCommand, UUID> {

    private final ClientGateway gateway;

    public CreateClientUseCase(ClientGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public UUID execute(CreateClientCommand cmd) {
        if (gateway.existsByEmail(cmd.email())) {
            throw new ClientExceptions.EmailExists();
        }

        if (gateway.existsByDocument(cmd.document())) {
            throw new ClientExceptions.DocumentExists();
        }

        Client client = Client.create(
                cmd.name(),
                cmd.email(),
                cmd.document(),
                cmd.phone()
        );

        client = gateway.create(client);

        return client.getId();
    }
}
