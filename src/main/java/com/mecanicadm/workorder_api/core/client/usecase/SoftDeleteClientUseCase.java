package com.mecanicadm.workorder_api.core.client.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.client.usecase.command.SoftDeleteClientCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class SoftDeleteClientUseCase implements VoidUseCase<SoftDeleteClientCommand> {

    private final ClientGateway gateway;

    public SoftDeleteClientUseCase(ClientGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(SoftDeleteClientCommand cmd) {
        Client client = gateway.findById(cmd.id())
                .orElseThrow(ClientExceptions.NotFound::new);

        client.delete();
        gateway.update(client);
    }
}
