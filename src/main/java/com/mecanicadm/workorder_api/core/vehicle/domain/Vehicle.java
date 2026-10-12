package com.mecanicadm.workorder_api.core.vehicle.domain;

import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.shared.domain.AuditDomain;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class Vehicle extends AuditDomain {
    private final String licensePlate;
    private final String model;
    private final String brand;
    private final Short modelYear;

    private Vehicle(String model, String licensePlate, String brand, Short modelYear) {
        this.model = model;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.modelYear = modelYear;
        validate();
    }

    public static Vehicle create(String model, String licensePlate, String brand, Short modelYear) {
        var vehicle = new Vehicle(model, licensePlate, brand, modelYear);
        vehicle.create();
        return vehicle;
    }

    @SuppressWarnings("java:S107")
    public static Vehicle restore(String model, String licensePlate, String brand, Short modelYear,
                                   LocalDateTime deletedAt, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        Vehicle vehicle = new Vehicle(model, licensePlate, brand, modelYear);
        vehicle.deletedAt = deletedAt;
        vehicle.dateCreated = dateCreated;
        vehicle.dateUpdated = dateUpdated;
        return vehicle;
    }

    public Vehicle updateInfo(String model, String brand, Short modelYear) {
        var vehicle = new Vehicle(model, this.getLicensePlate(), brand, modelYear);
        vehicle.update();
        return vehicle;
    }

    public Vehicle update(String model, String brand, Short modelYear) {
        return updateInfo(model, brand, modelYear);
    }

    private void validate() {
        validateModel();
        validateBrand();
        validateModelYear();
    }

    private void validateModel() {
        if (isBlank(this.model)) {
            throw new VehicleExceptions.ModelNotEmpty();
        }
    }

    private void validateBrand() {
        if (isBlank(this.brand)) {
            throw new VehicleExceptions.BrandNotEmpty();
        }
    }

    private void validateModelYear() {
        if (isNull(this.modelYear)) {
            throw new VehicleExceptions.InvalidModelYear();
        }
        if (this.modelYear < 1886 || this.modelYear > 9999) {
            throw new VehicleExceptions.InvalidModelYear();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public String getLicensePlate() { return licensePlate; }
    public String getModel() { return model; }
    public String getBrand() { return brand; }
    public Short getModelYear() { return modelYear; }
}

