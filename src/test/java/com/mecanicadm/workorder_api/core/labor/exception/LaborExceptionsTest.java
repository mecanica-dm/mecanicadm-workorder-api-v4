package com.mecanicadm.workorder_api.core.labor.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LaborExceptionsTest {

    @Test
    @DisplayName("Deve cobrir o construtor privado de LaborExceptions")
    void shouldCoverPrivateConstructor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<LaborExceptions> constructor = LaborExceptions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        LaborExceptions instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    @DisplayName("Deve instanciar todas as exceções de Labor para garantir cobertura")
    void shouldInstantiateAllExceptions() {
        var notFound = new LaborExceptions.LaborNotFound();
        assertEquals(404, notFound.getStatus());
        assertEquals("labor.not.found", notFound.getMessageKey());
    }
}
