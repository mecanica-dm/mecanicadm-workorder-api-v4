package com.mecanicadm.workorder_api.infra.config;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("messageSource")
public class Utf8MessageSource extends ReloadableResourceBundleMessageSource {

    public Utf8MessageSource() {
        setBasename("classpath:i18n/messages");
        setDefaultEncoding("UTF-8");
        setUseCodeAsDefaultMessage(true);
        setDefaultLocale(Locale.of("pt", "BR"));
        setFallbackToSystemLocale(false);
    }
}