package com.mecanicadm.workorder_api.core.vehicle.usecase.command;

public record UpdateVehicleCommand(String licensePlate, String model, String brand, Short modelYear) {
}

