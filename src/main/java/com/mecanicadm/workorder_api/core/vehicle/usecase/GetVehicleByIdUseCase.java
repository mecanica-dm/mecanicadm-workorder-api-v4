package com.mecanicadm.workorder_api.core.vehicle.usecase;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.core.vehicle.usecase.query.GetVehicleByIdQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.Optional;

public class GetVehicleByIdUseCase implements UseCase<GetVehicleByIdQuery, Vehicle> {

    private final VehicleGateway gateway;

    public GetVehicleByIdUseCase(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Vehicle execute(GetVehicleByIdQuery query) {
        Optional<Vehicle> opt = gateway.findByLicensePlate(query.licensePlate());
        return opt.orElseThrow(VehicleExceptions.NotFound::new);
    }
}

