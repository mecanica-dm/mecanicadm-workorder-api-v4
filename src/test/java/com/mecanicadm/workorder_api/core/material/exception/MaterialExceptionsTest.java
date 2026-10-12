package com.mecanicadm.workorder_api.core.material.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaterialExceptionsTest {

    @Test
    @DisplayName("Deve cobrir o construtor privado de MaterialExceptions")
    void shouldCoverPrivateConstructor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<MaterialExceptions> constructor = MaterialExceptions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        MaterialExceptions instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    @DisplayName("Deve instanciar todas as exceções de Material para garantir cobertura")
    void shouldInstantiateAllExceptions() {
        var notFound = new MaterialExceptions.MaterialNotFound();
        assertEquals(404, notFound.getStatus());
        assertEquals("material.not.found", notFound.getMessageKey());
    }
}
