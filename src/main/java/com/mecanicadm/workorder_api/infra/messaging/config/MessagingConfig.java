package com.mecanicadm.workorder_api.infra.messaging.config;

import com.mecanicadm.workorder_api.infra.messaging.outbox.OutboxProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OutboxProperties.class)
public class MessagingConfig {
}
