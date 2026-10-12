package com.mecanicadm.workorder_api.infra.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private SecurityFilter securityFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    @DisplayName("Deve criar PasswordEncoder com BCrypt")
    void shouldCreatePasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
        assertTrue(encoder.matches("Senha123", encoder.encode("Senha123")));
    }
}
