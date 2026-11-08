package com.mecanicadm.workorder_api.core.user.domain.enums;

import java.util.Locale;

public enum TokenRole {
    CLIENT,
    USER;

    public static TokenRole fromClaim(String role) {
        if (role == null || role.isBlank()) {
            return USER;
        }
        try {
            return valueOf(role.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return USER;
        }
    }
}