package com.mecanicadm.workorder_api.infra.features.user.api.dto;

import com.mecanicadm.workorder_api.core.user.usecase.dto.AuthenticateUserResponse;

public record AuthenticationResponse(
        String access_token,
        String userId,
        String name,
        String email
) {
    public static AuthenticationResponse from(AuthenticateUserResponse auth) {
        return new AuthenticationResponse(auth.token(), auth.userId().toString(), auth.name(), auth.email());
    }
}
