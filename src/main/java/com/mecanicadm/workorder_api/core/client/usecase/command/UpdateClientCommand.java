package com.mecanicadm.workorder_api.core.client.usecase.command;

import java.util.UUID;

public record UpdateClientCommand(
        UUID id,
        String name,
        String email,
        String document,
        String phone
) {
}
