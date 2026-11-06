package com.mecanicadm.workorder_api.infra.security.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidTokenExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem padrão")
    void shouldCreateWithDefaultMessage() {
        var exception = new InvalidTokenException();
        assertEquals("token.invalid.expired", exception.getMessage());
    }
}
