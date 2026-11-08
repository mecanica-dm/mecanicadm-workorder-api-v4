package com.mecanicadm.workorder_api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mecanicadm.workorder_api.core.user.domain.enums.TokenRole;
import com.mecanicadm.workorder_api.infra.security.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class TokenServiceTest {

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret");
    }

    @Test
    @DisplayName("Deve gerar um token válido para um e-mail")
    void shouldGenerateToken() {
        String email = "user@test.com";
        String token = tokenService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals(email, decodedJWT.getSubject());
        assertEquals("workorder_api", decodedJWT.getIssuer());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando ocorrer erro na criação do token")
    void shouldThrowRuntimeExceptionWhenTokenCreationFails() {
        try (MockedStatic<JWT> jwtMockedStatic = mockStatic(JWT.class)) {
            jwtMockedStatic.when(JWT::create).thenThrow(new JWTCreationException("Mocked error", new RuntimeException()));

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    tokenService.generateToken("user@test.com")
            );

            assertEquals("token.generation.error", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Deve validar um token gerado corretamente")
    void shouldValidateCorrectToken() {
        String email = "user@test.com";
        String token = tokenService.generateToken(email);

        String subject = tokenService.validateToken(token);

        assertEquals(email, subject);
    }

    @Test
    @DisplayName("Deve decodificar as claims do token validado")
    void shouldDecodeTokenClaims() {
        String subject = "52998224725";

        DecodedJWT decoded = JWT.decode(JWT.create()
                .withIssuer("workorder_api")
                .withSubject(subject)
                .withClaim("role", "CLIENT")
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 3600_000))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256("test-secret")));

        var claims = tokenService.decodeToken(decoded.getToken());

        assertEquals(subject, claims.subject());
        assertEquals(TokenRole.CLIENT, claims.role());
    }

    @Test
    @DisplayName("Deve decodificar token de usuário sem claim de role")
    void shouldDecodeTokenWithoutRole() {
        String email = "user@test.com";
        String token = tokenService.generateToken(email);

        var claims = tokenService.decodeToken(token);

        assertEquals(email, claims.subject());
        assertEquals(TokenRole.USER, claims.role());
    }

    @Test
    @DisplayName("Deve lançar InvalidTokenException para token com issuer diferente")
    void shouldThrowExceptionForTokenWithDifferentIssuer() {
        DecodedJWT decoded = JWT.decode(JWT.create()
                .withIssuer("outro-emissor")
                .withSubject("user@test.com")
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 3600_000))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256("test-secret")));

        assertThrows(InvalidTokenException.class, () ->
                tokenService.decodeToken(decoded.getToken())
        );
    }

    @Test
    @DisplayName("Deve lançar InvalidTokenException para token inválido")
    void shouldThrowExceptionForInvalidToken() {
        assertThrows(InvalidTokenException.class, () ->
            tokenService.validateToken("invalid-token")
        );
    }

    @Test
    @DisplayName("Deve lançar InvalidTokenException para token expirado ou com secret diferente")
    void shouldThrowExceptionForModifiedToken() {
        String token = tokenService.generateToken("user@test.com");

        ReflectionTestUtils.setField(tokenService, "secret", "another-secret");

        assertThrows(InvalidTokenException.class, () ->
            tokenService.validateToken(token)
        );
    }
}
