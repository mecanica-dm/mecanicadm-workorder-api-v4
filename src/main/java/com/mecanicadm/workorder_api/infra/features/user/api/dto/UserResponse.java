package com.mecanicadm.workorder_api.infra.features.user.api.dto;

import com.mecanicadm.workorder_api.core.user.domain.User;

public record UserResponse(
        String email,
        String name
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.getEmail(), user.getName());
    }
}
