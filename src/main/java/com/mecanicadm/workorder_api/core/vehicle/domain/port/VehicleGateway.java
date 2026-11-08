package com.mecanicadm.workorder_api.core.vehicle.domain.port;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;

import java.util.Optional;

public interface VehicleGateway {
    Vehicle create(Vehicle vehicle);

    Vehicle update(Vehicle vehicle);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    VehiclePageResult findAll(VehiclePageQuery query);

    boolean existsByLicensePlate(String licensePlate);
}
