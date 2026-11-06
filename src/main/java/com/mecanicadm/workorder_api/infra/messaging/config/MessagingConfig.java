package com.mecanicadm.workorder_api.infra.messaging.config;

import com.mecanicadm.workorder_api.infra.messaging.outbox.OutboxProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableConfigurationProperties(OutboxProperties.class)
public class MessagingConfig {

    @Configuration
    @EnableScheduling
    @ConditionalOnProperty(name = "messaging.outbox.scheduler.enabled", havingValue = "true", matchIfMissing = true)
    static class OutboxSchedulingConfig {
    }
}
