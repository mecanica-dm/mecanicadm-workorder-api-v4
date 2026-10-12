package com.mecanicadm.workorder_api.infra.security;

import com.mecanicadm.workorder_api.core.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar instanciar classe utilitária")
    void shouldThrowExceptionWhenInstantiatingUtilityClass() throws NoSuchMethodException {
        Constructor<SecurityUtils> constructor = SecurityUtils.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertEquals("Utility class cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Deve retornar o usuário autenticado")
    void shouldReturnAuthenticatedUser() {
        User user = User.create("test@test.com", "password123", "Test User");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        User authenticatedUser = SecurityUtils.getAuthenticatedUser();

        assertEquals(user, authenticatedUser);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não houver objeto de autenticação")
    void shouldThrowExceptionWhenAuthenticationIsNull() {
        SecurityContextHolder.clearContext();
        assertThrows(RuntimeException.class, SecurityUtils::getAuthenticatedUser);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o objeto de autenticação não estiver autenticado")
    void shouldThrowExceptionWhenNotAuthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(RuntimeException.class, SecurityUtils::getAuthenticatedUser);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o principal não for do tipo User")
    void shouldThrowExceptionWhenPrincipalIsNotUser() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("not-a-user-object", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(RuntimeException.class, SecurityUtils::getAuthenticatedUser);
    }
}
