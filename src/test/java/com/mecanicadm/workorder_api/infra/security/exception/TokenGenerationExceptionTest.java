package com.mecanicadm.workorder_api.infra.security.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenGenerationExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com messageKey e causa")
    void shouldCreateWithMessageKeyAndCause() {
        var cause = new RuntimeException("erro");
        var exception = new TokenGenerationException("token.generation.error", cause);

        assertEquals("token.generation.error", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
