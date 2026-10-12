package com.mecanicadm.workorder_api.core.user.usecase.command;

import java.util.UUID;

public record UpdateUserCommand(
        UUID id,
        String name,
        String password,
        String currentPassword
) {
}
