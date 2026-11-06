package com.mecanicadm.workorder_api.core.vehicle.usecase;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.core.vehicle.usecase.command.DeleteVehicleCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class DeleteVehicleUseCase implements VoidUseCase<DeleteVehicleCommand> {

    private final VehicleGateway gateway;

    public DeleteVehicleUseCase(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(DeleteVehicleCommand command) {
        Vehicle vehicle = gateway.findByLicensePlate(command.licensePlate())
                .orElseThrow(VehicleExceptions.NotFound::new);
        vehicle.delete();
        gateway.update(vehicle);
    }
}

