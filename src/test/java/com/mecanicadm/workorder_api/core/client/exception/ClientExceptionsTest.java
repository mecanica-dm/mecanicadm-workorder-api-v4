package com.mecanicadm.workorder_api.core.client.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClientExceptionsTest {

    @Test
    @DisplayName("Deve cobrir o construtor privado de ClientExceptions")
    void shouldCoverPrivateConstructor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<ClientExceptions> constructor = ClientExceptions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ClientExceptions instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    @DisplayName("Deve instanciar todas as exceções de Client para garantir cobertura")
    void shouldInstantiateAllExceptions() {
        var notFound = new ClientExceptions.NotFound();
        assertEquals(404, notFound.getStatus());
        assertEquals("client.not.found", notFound.getMessageKey());

        var documentExists = new ClientExceptions.DocumentExists();
        assertEquals(409, documentExists.getStatus());
        assertEquals("client.document.exists", documentExists.getMessageKey());

        var emailExists = new ClientExceptions.EmailExists();
        assertEquals(409, emailExists.getStatus());
        assertEquals("client.email.exists", emailExists.getMessageKey());

        var nameNotEmpty = new ClientExceptions.NameNotEmpty();
        assertEquals(400, nameNotEmpty.getStatus());
        assertEquals("client.name.not.empty", nameNotEmpty.getMessageKey());

        var emailNotEmpty = new ClientExceptions.EmailNotEmpty();
        assertEquals(400, emailNotEmpty.getStatus());
        assertEquals("client.email.not.empty", emailNotEmpty.getMessageKey());

        var documentNotEmpty = new ClientExceptions.DocumentNotEmpty();
        assertEquals(400, documentNotEmpty.getStatus());
        assertEquals("client.document.not.empty", documentNotEmpty.getMessageKey());
    }
}
