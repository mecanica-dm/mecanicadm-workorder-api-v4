package com.mecanicadm.workorder_api.core.user.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenRoleTest {

    @Test
    @DisplayName("Deve mapear a claim 'CLIENT' para TokenRole.CLIENT")
    void shouldMapClientClaim() {
        assertEquals(TokenRole.CLIENT, TokenRole.fromClaim("CLIENT"));
        assertEquals(TokenRole.CLIENT, TokenRole.fromClaim("client"));
    }

    @Test
    @DisplayName("Deve tratar ausência de role como usuário")
    void shouldDefaultToUserWhenRoleMissingOrUnknown() {
        assertEquals(TokenRole.USER, TokenRole.fromClaim(null));
        assertEquals(TokenRole.USER, TokenRole.fromClaim(""));
        assertEquals(TokenRole.USER, TokenRole.fromClaim("  "));
        assertEquals(TokenRole.USER, TokenRole.fromClaim("ADMIN"));
    }
}