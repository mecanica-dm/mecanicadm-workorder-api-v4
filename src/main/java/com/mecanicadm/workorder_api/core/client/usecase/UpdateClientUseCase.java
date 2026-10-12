package com.mecanicadm.workorder_api.core.client.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.client.usecase.command.UpdateClientCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import java.util.UUID;

public class UpdateClientUseCase implements VoidUseCase<UpdateClientCommand> {

    private final ClientGateway gateway;

    public UpdateClientUseCase(ClientGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(UpdateClientCommand cmd) {
        Client client = gateway.findById(cmd.id())
                .orElseThrow(ClientExceptions.NotFound::new);

        validate(cmd, client.getId());

        client.update(
                cmd.name(),
                cmd.email(),
                cmd.document(),
                cmd.phone()
        );

        gateway.update(client);
    }

    private void validate(UpdateClientCommand cmd, UUID clientId) {
        if (gateway.existsByEmailAndIdNot(cmd.email(), clientId)) {
            throw new ClientExceptions.EmailExists();
        }

        if (gateway.existsByDocumentAndIdNot(cmd.document(), clientId)) {
            throw new ClientExceptions.DocumentExists();
        }
    }
}
