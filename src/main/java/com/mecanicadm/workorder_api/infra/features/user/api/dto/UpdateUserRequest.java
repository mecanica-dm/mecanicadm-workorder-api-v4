package com.mecanicadm.workorder_api.infra.features.user.api.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        String name,
        @Size(min = 6) String password,
        String currentPassword
) {}
