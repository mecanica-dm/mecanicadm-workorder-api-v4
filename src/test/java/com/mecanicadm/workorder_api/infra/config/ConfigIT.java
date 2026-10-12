package com.mecanicadm.workorder_api.infra.config;

import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
class ConfigIT extends AbstractIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("Deve configurar I18nConfig corretamente")
    @SuppressWarnings("unchecked")
    void shouldLoadI18nConfig() {
        LocaleResolver localeResolver = context.getBean(LocaleResolver.class);
        assertNotNull(localeResolver);
        assertInstanceOf(AcceptHeaderLocaleResolver.class, localeResolver);
        
        AcceptHeaderLocaleResolver resolver = (AcceptHeaderLocaleResolver) localeResolver;
        Locale defaultLocale = (Locale) ReflectionTestUtils.getField(resolver, "defaultLocale");
        assertEquals(Locale.of("pt", "BR"), defaultLocale);
        
        List<Locale> supportedLocales = (List<Locale>) ReflectionTestUtils.getField(resolver, "supportedLocales");
        assertNotNull(supportedLocales);
        assertTrue(supportedLocales.contains(Locale.of("en")));
    }

    @Test
    @DisplayName("Deve configurar OpenApiConfig e executar o OperationCustomizer")
    void shouldLoadOpenApiConfigAndExecuteCustomizer() {
        OpenAPI openAPI = context.getBean(OpenAPI.class);
        assertNotNull(openAPI);
        assertEquals("Mecânica DM API", openAPI.getInfo().getTitle());
        
        OperationCustomizer customizer = (OperationCustomizer) context.getBean("addAcceptLanguageHeader");
        assertNotNull(customizer);

        HandlerMethod handlerMethod = mock(HandlerMethod.class);

        Operation op1 = new Operation();
        customizer.customize(op1, handlerMethod);
        assertTrue(op1.getParameters().stream().anyMatch(p -> "Accept-Language".equals(p.getName())));

        Operation op2 = new Operation();
        op2.setParameters(new ArrayList<>());
        op2.addParametersItem(new Parameter().name("Existing-Param"));
        
        customizer.customize(op2, handlerMethod);
        
        assertEquals(2, op2.getParameters().size());
        assertTrue(op2.getParameters().stream().anyMatch(p -> "Accept-Language".equals(p.getName())));
        assertTrue(op2.getParameters().stream().anyMatch(p -> "Existing-Param".equals(p.getName())));
    }
}
