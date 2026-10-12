package com.mecanicadm.workorder_api.infra.security;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.infra.security.exception.UserNotAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
            throw new UserNotAuthenticatedException();
        }
        return (User) authentication.getPrincipal();
    }
}
