package com.mecanicadm.workorder_api.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

@Configuration
public class I18nConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        Locale defaultLocale = Locale.of("pt", "BR");
        localeResolver.setDefaultLocale(defaultLocale);
        localeResolver.setSupportedLocales(Arrays.asList(
                defaultLocale,
                Locale.of("en"),
                Locale.of("es")
        ));
        return localeResolver;
    }
}