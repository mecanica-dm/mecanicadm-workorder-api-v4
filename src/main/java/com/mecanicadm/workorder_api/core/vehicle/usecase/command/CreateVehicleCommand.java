package com.mecanicadm.workorder_api.core.vehicle.usecase.command;

public record CreateVehicleCommand(String model,
                                   String licensePlate,
                                   String brand,
                                   Short modelYear) {
}
