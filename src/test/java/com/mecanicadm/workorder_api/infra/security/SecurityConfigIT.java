package com.mecanicadm.workorder_api.infra.security;

import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityConfigIT extends AbstractIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("Deve carregar os Beans de segurança corretamente")
    void shouldLoadSecurityBeans() {
        assertNotNull(context.getBean(SecurityFilterChain.class));
        assertNotNull(context.getBean(AuthenticationManager.class));
        assertNotNull(context.getBean(PasswordEncoder.class));
        assertInstanceOf(BCryptPasswordEncoder.class, context.getBean(PasswordEncoder.class));
    }
}
