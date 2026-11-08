package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.core.labor.usecase.query.GetLaborByIdQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

public class GetLaborByIdUseCase implements UseCase<GetLaborByIdQuery, Labor> {

    private final LaborGateway gateway;

    public GetLaborByIdUseCase(LaborGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Labor execute(GetLaborByIdQuery query) {
        return gateway.findById(query.id()).orElseThrow(LaborExceptions.LaborNotFound::new);
    }
}
