package com.mecanicadm.workorder_api.core.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PasswordResetTokenTest {

    @Test
    @DisplayName("Deve identificar se o token expirou")
    void shouldCheckExpiration() {
        User user = mock(User.class);
        LocalDateTime pastDate = LocalDateTime.now().minusMinutes(1);
        LocalDateTime futureDate = LocalDateTime.now().plusMinutes(10);

        PasswordResetToken expiredToken = new PasswordResetToken("token1", user, pastDate);
        PasswordResetToken validToken = new PasswordResetToken("token2", user, futureDate);

        assertTrue(expiredToken.isExpired());
        assertFalse(validToken.isExpired());
    }

    @Test
    @DisplayName("Deve retornar os dados corretos do token")
    void shouldReturnTokenData() {
        User user = mock(User.class);
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        PasswordResetToken token = new PasswordResetToken("abc-123", user, expiry);

        assertEquals("abc-123", token.getToken());
        assertEquals(user, token.getUser());
        assertEquals(expiry, token.getExpiryDate());
    }
}
