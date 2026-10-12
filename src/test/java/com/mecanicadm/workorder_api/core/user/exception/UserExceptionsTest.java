package com.mecanicadm.workorder_api.core.user.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserExceptionsTest {

    @Test
    @DisplayName("Deve cobrir o construtor privado de UserExceptions")
    void shouldCoverPrivateConstructor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<UserExceptions> constructor = UserExceptions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        UserExceptions instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    @DisplayName("Deve instanciar todas as exceções de User para garantir cobertura")
    void shouldInstantiateAllExceptions() {
        var notFound = new UserExceptions.NotFound();
        assertEquals(404, notFound.getStatus());
        assertEquals("user.not.found", notFound.getMessageKey());

        var emailExists = new UserExceptions.EmailExists();
        assertEquals(409, emailExists.getStatus());
        assertEquals("user.email.exists", emailExists.getMessageKey());

        var userAlreadyExists = new UserExceptions.UserAlreadyExists();
        assertEquals(409, userAlreadyExists.getStatus());
        assertEquals("user.already.exists", userAlreadyExists.getMessageKey());

        var emailNotEmpty = new UserExceptions.EmailNotEmpty();
        assertEquals(400, emailNotEmpty.getStatus());
        assertEquals("user.email.not.empty", emailNotEmpty.getMessageKey());

        var passwordMinLength = new UserExceptions.PasswordMinLength();
        assertEquals(400, passwordMinLength.getStatus());
        assertEquals("user.password.min.length", passwordMinLength.getMessageKey());

        var currentPasswordRequired = new UserExceptions.CurrentPasswordRequired();
        assertEquals(400, currentPasswordRequired.getStatus());
        assertEquals("user.password.current.required", currentPasswordRequired.getMessageKey());

        var badCredentials = new UserExceptions.BadCredentials();
        assertEquals(401, badCredentials.getStatus());
        assertEquals("error.bad.credentials", badCredentials.getMessageKey());

        var tokenInvalid = new UserExceptions.TokenInvalid();
        assertEquals(400, tokenInvalid.getStatus());
        assertEquals("token.invalid", tokenInvalid.getMessageKey());

        var tokenExpired = new UserExceptions.TokenExpired();
        assertEquals(400, tokenExpired.getStatus());
        assertEquals("token.expired", tokenExpired.getMessageKey());
    }
}