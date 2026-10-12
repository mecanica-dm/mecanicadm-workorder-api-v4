package com.mecanicadm.workorder_api.core.user.exception;

import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;

public class UserExceptions {

    private UserExceptions() {
    }

    public static class NotFound extends DomainExceptionCore {
        public NotFound() {
            super("user.not.found", 404);
        }
    }

    public static class UserAlreadyExists extends DomainExceptionCore {
        public UserAlreadyExists() {
            super("user.already.exists", 409);
        }
    }

    public static class EmailExists extends DomainExceptionCore {
        public EmailExists() {
            super("user.email.exists", 409);
        }
    }

    public static class EmailNotEmpty extends DomainExceptionCore {
        public EmailNotEmpty() {
            super("user.email.not.empty", 400);
        }
    }

    public static class PasswordMinLength extends DomainExceptionCore {
        public PasswordMinLength() {
            super("user.password.min.length", 400);
        }
    }

    public static class CurrentPasswordRequired extends DomainExceptionCore {
        public CurrentPasswordRequired() {
            super("user.password.current.required", 400);
        }
    }

    public static class BadCredentials extends DomainExceptionCore {
        public BadCredentials() {
            super("error.bad.credentials", 401);
        }
    }

    public static class TokenInvalid extends DomainExceptionCore {
        public TokenInvalid() {
            super("token.invalid", 400);
        }
    }

    public static class TokenExpired extends DomainExceptionCore {
        public TokenExpired() {
            super("token.expired", 400);
        }
    }
}
