package com.mecanicadm.workorder_api.infra.security.exception;

public class InvalidTokenException extends SecurityException {
    public InvalidTokenException() {
        super("token.invalid.expired");
    }
}
