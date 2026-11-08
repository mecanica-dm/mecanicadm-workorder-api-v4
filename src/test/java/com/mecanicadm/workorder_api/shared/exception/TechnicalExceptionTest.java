package com.mecanicadm.workorder_api.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TechnicalExceptionTest {

    @Test
    @DisplayName("Deve armazenar code e args corretamente")
    void shouldStoreCodeAndArgs() {
        var exception = new TechnicalException("error.code", "param1", 42);

        assertEquals("error.code", exception.getCode());
        assertEquals("error.code", exception.getMessage());
        assertArrayEquals(new Object[]{"param1", 42}, exception.getArgs());
    }

    @Test
    @DisplayName("Deve suportar args vazios")
    void shouldSupportEmptyArgs() {
        var exception = new TechnicalException("error.code");

        assertEquals("error.code", exception.getCode());
        assertArrayEquals(new Object[0], exception.getArgs());
    }
}
