package com.mecanicadm.workorder_api.infra.features.vehicle.persistence.entity;

import com.mecanicadm.workorder_api.infra.audit.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "vehicle")
@SQLRestriction("deleted_at IS NULL")
public class VehicleJpaEntity extends AuditEntity {

    @Id
    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    @Column(name = "model")
    private String model;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model_year")
    private Short modelYear;

    protected VehicleJpaEntity() {}

    public VehicleJpaEntity(String licensePlate, String model, String brand, Short modelYear) {
        this.licensePlate = licensePlate;
        this.model = model;
        this.brand = brand;
        this.modelYear = modelYear;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Short getModelYear() {
        return modelYear;
    }

    public void setModelYear(Short modelYear) {
        this.modelYear = modelYear;
    }
}

