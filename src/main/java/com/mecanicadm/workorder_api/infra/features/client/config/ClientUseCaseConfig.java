package com.mecanicadm.workorder_api.infra.features.client.config;

import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.usecase.CreateClientUseCase;
import com.mecanicadm.workorder_api.core.client.usecase.GetAllClientUseCase;
import com.mecanicadm.workorder_api.core.client.usecase.SoftDeleteClientUseCase;
import com.mecanicadm.workorder_api.core.client.usecase.UpdateClientUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientUseCaseConfig {

    @Bean
    public CreateClientUseCase createClientUseCase(ClientGateway gateway) {
        return new CreateClientUseCase(gateway);
    }

    @Bean
    public UpdateClientUseCase updateClientUseCase(ClientGateway gateway) {
        return new UpdateClientUseCase(gateway);
    }

    @Bean
    public GetAllClientUseCase getAllClientUseCase(ClientGateway gateway) {
        return new GetAllClientUseCase(gateway);
    }

    @Bean
    public SoftDeleteClientUseCase softDeleteClientUseCase(ClientGateway gateway) {
        return new SoftDeleteClientUseCase(gateway);
    }
}
