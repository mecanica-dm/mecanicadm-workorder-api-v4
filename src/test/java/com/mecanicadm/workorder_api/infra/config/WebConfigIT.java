package com.mecanicadm.workorder_api.infra.config;

import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WebConfigIT extends AbstractIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("Deve carregar a WebConfig no contexto do Spring")
    void shouldLoadWebConfig() {
        WebConfig webConfig = context.getBean(WebConfig.class);
        assertNotNull(webConfig);
    }

    @Test
    @DisplayName("WebConfig deve ter a anotacao EnableSpringDataWebSupport via DTO")
    void shouldHaveEnableSpringDataWebSupportAnnotation() {
        EnableSpringDataWebSupport annotation = WebConfig.class.getAnnotation(EnableSpringDataWebSupport.class);
        
        assertNotNull(annotation, "A classe WebConfig deve possuir a anotação @EnableSpringDataWebSupport");
        assertEquals(EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO, annotation.pageSerializationMode());
    }
}
