package com.mecanicadm.workorder_api.core.user.usecase.dto;

import java.util.UUID;

public record AuthenticateUserResponse(String token, UUID userId, String name, String email) {
}
