package com.mecanicadm.workorder_api.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionCoreTest {

    @Test
    @DisplayName("Deve armazenar messageKey, status e args corretamente")
    void shouldStoreMessageKeyStatusAndArgs() {
        var exception = new TestDomainException("test.key", 400, "arg1", "arg2");

        assertEquals("test.key", exception.getMessageKey());
        assertEquals(400, exception.getStatus());
        assertArrayEquals(new Object[]{"arg1", "arg2"}, exception.getArgs());
    }

    @Test
    @DisplayName("Deve suportar args vazios")
    void shouldSupportEmptyArgs() {
        var exception = new TestDomainException("test.key", 500);

        assertEquals("test.key", exception.getMessageKey());
        assertEquals(500, exception.getStatus());
        assertArrayEquals(new Object[0], exception.getArgs());
    }

    private static class TestDomainException extends DomainExceptionCore {
        protected TestDomainException(String messageKey, int status, Object... args) {
            super(messageKey, status, args);
        }
    }
}
