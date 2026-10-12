package com.mecanicadm.workorder_api.core.vehicle.usecase;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.core.vehicle.usecase.command.UpdateVehicleCommand;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

public class UpdateVehicleUseCase implements UseCase<UpdateVehicleCommand, Vehicle> {

    private final VehicleGateway gateway;

    public UpdateVehicleUseCase(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Vehicle execute(UpdateVehicleCommand command) {
        Vehicle vehicle = gateway.findByLicensePlate(command.licensePlate())
                .orElseThrow(VehicleExceptions.NotFound::new);
        Vehicle updated = vehicle.update(command.model(), command.brand(), command.modelYear());
        return gateway.update(updated);
    }
}

