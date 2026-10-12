package com.mecanicadm.workorder_api.core.vehicle.usecase;

import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleFilter;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehiclePageQuery;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehiclePageResult;
import com.mecanicadm.workorder_api.core.vehicle.usecase.query.GetAllVehiclesQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

public class GetAllVehicleUseCase implements UseCase<GetAllVehiclesQuery, VehiclePageResult> {

    private final VehicleGateway gateway;

    public GetAllVehicleUseCase(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public VehiclePageResult execute(GetAllVehiclesQuery query) {
        VehicleFilter filter = new VehicleFilter(query.licensePlate(), query.model(), query.brand(), query.modelYear());
        VehiclePageQuery pageQuery = new VehiclePageQuery(filter, query.page(), query.size(), query.sortBy(), query.direction());
        return gateway.findAll(pageQuery);
    }
}

