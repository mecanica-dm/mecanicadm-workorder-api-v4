package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.usecase.command.CreateMaterialCommand;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.AddStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.AddStockCommand;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.UUID;

public class CreateMaterialUseCase implements UseCase<CreateMaterialCommand, UUID> {

    private final MaterialGateway gateway;
    private final AddStockUseCase addStockUseCase;

    public CreateMaterialUseCase(MaterialGateway gateway, AddStockUseCase addStockUseCase) {
        this.gateway = gateway;
        this.addStockUseCase = addStockUseCase;
    }

    @Override
    public UUID execute(CreateMaterialCommand cmd) {
        Material material = Material.create(
                cmd.name(),
                cmd.brand(),
                cmd.description(),
                cmd.price(),
                cmd.type()
        );

        material = gateway.create(material);
        addStockUseCase.execute(new AddStockCommand(material.getId(), cmd.quantity()));

        return material.getId();
    }
}
