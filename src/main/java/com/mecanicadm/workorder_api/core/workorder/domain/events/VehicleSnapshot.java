package com.mecanicadm.workorder_api.core.workorder.domain.events;

public record VehicleSnapshot(
        String licensePlate,
        String model,
        String brand,
        int modelYear) {
}
