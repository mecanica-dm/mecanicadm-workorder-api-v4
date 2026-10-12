package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.exception.MaterialExceptions;
import com.mecanicadm.workorder_api.core.material.usecase.command.SoftDeleteMaterialCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class SoftDeleteMaterialUseCase implements VoidUseCase<SoftDeleteMaterialCommand> {

    private final MaterialGateway gateway;

    public SoftDeleteMaterialUseCase(MaterialGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(SoftDeleteMaterialCommand cmd) {
        Material material = gateway.findById(cmd.id())
                .orElseThrow(MaterialExceptions.MaterialNotFound::new);
        material.delete();
        gateway.update(material);
    }
}
