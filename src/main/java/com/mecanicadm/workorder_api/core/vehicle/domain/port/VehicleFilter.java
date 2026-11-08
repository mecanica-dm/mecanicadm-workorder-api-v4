package com.mecanicadm.workorder_api.core.vehicle.domain.port;

public record VehicleFilter(
        String licensePlate,
        String model,
        String brand,
        Short modelYear
) {
}

