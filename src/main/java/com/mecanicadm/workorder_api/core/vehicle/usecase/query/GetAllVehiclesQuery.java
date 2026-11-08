package com.mecanicadm.workorder_api.core.vehicle.usecase.query;

public record GetAllVehiclesQuery(
        String licensePlate,
        String model,
        String brand,
        Short modelYear,
        int page,
        int size,
        String sortBy,
        String direction
) {
}
