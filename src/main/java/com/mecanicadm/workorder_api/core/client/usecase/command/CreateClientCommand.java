package com.mecanicadm.workorder_api.core.client.usecase.command;

public record CreateClientCommand(
        String name,
        String email,
        String document,
        String phone
) {
}
