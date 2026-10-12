package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.exception.MaterialExceptions;
import com.mecanicadm.workorder_api.core.material.usecase.command.UpdateMaterialCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class UpdateMaterialUseCase implements VoidUseCase<UpdateMaterialCommand> {

    private final MaterialGateway gateway;

    public UpdateMaterialUseCase(MaterialGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(UpdateMaterialCommand cmd) {
        Material material = gateway.findById(cmd.id())
                .orElseThrow(MaterialExceptions.MaterialNotFound::new);

        material.update(
                cmd.name(),
                cmd.brand(),
                cmd.description(),
                cmd.price(),
                cmd.type()
        );

        gateway.update(material);
    }
}
