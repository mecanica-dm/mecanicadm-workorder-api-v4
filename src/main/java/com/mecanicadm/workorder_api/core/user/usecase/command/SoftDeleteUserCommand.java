package com.mecanicadm.workorder_api.core.user.usecase.command;

import java.util.UUID;

public record SoftDeleteUserCommand(UUID id) {
}
