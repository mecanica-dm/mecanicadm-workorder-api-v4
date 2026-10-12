package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.exception.MaterialExceptions;
import com.mecanicadm.workorder_api.core.material.usecase.query.GetMaterialByIdQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

public class GetMaterialByIdUseCase implements UseCase<GetMaterialByIdQuery, Material> {

    private final MaterialGateway gateway;

    public GetMaterialByIdUseCase(MaterialGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Material execute(GetMaterialByIdQuery query) {
        return gateway.findById(query.id())
                .orElseThrow(MaterialExceptions.MaterialNotFound::new);
    }
}
