package com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.response;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;

public record VehicleResponse(String model, String licensePlate, String brand, Short modelYear) {
    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(vehicle.getModel(), vehicle.getLicensePlate(), vehicle.getBrand(), vehicle.getModelYear());
    }
}

