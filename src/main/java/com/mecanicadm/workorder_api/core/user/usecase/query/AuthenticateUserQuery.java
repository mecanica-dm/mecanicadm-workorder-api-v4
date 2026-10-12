package com.mecanicadm.workorder_api.core.user.usecase.query;

public record AuthenticateUserQuery(
        String email,
        String password
) {
}
