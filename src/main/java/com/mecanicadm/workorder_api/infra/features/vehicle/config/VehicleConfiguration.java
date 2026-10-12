package com.mecanicadm.workorder_api.infra.features.vehicle.config;

import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VehicleConfiguration {

    @Bean
    public CreateVehicleUseCase createVehicleUseCase(VehicleGateway gateway) {
        return new CreateVehicleUseCase(gateway);
    }

    @Bean
    public UpdateVehicleUseCase updateVehicleUseCase(VehicleGateway gateway) {
        return new UpdateVehicleUseCase(gateway);
    }

    @Bean
    public DeleteVehicleUseCase deleteVehicleUseCase(VehicleGateway gateway) {
        return new DeleteVehicleUseCase(gateway);
    }

    @Bean
    public GetVehicleByIdUseCase getVehicleByIdUseCase(VehicleGateway gateway) {
        return new GetVehicleByIdUseCase(gateway);
    }

    @Bean
    public GetAllVehicleUseCase getAllVehicleUseCase(VehicleGateway gateway) {
        return new GetAllVehicleUseCase(gateway);
    }
}

