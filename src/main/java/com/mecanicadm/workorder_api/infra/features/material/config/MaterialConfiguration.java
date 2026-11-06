package com.mecanicadm.workorder_api.infra.features.material.config;

import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.usecase.CreateMaterialUseCase;
import com.mecanicadm.workorder_api.core.material.usecase.GetAllMaterialsUseCase;
import com.mecanicadm.workorder_api.core.material.usecase.GetMaterialByIdUseCase;
import com.mecanicadm.workorder_api.core.material.usecase.SoftDeleteMaterialUseCase;
import com.mecanicadm.workorder_api.core.material.usecase.UpdateMaterialUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.AddStockUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaterialConfiguration {

    @Bean
    public CreateMaterialUseCase createMaterialUseCase(MaterialGateway gateway, AddStockUseCase addStockUseCase) {
        return new CreateMaterialUseCase(gateway, addStockUseCase);
    }

    @Bean
    public UpdateMaterialUseCase updateMaterialUseCase(MaterialGateway gateway) {
        return new UpdateMaterialUseCase(gateway);
    }

    @Bean
    public SoftDeleteMaterialUseCase softDeleteMaterialUseCase(MaterialGateway gateway) {
        return new SoftDeleteMaterialUseCase(gateway);
    }

    @Bean
    public GetMaterialByIdUseCase getMaterialByIdUseCase(MaterialGateway gateway) {
        return new GetMaterialByIdUseCase(gateway);
    }

    @Bean
    public GetAllMaterialsUseCase getAllMaterialsUseCase(MaterialGateway gateway) {
        return new GetAllMaterialsUseCase(gateway);
    }
}
