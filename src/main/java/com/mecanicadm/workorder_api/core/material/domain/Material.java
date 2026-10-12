package com.mecanicadm.workorder_api.core.material.domain;

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.material.exception.MaterialExceptions;
import com.mecanicadm.workorder_api.shared.domain.AuditDomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Material extends AuditDomain {

    private final UUID id;

    private String name;

    private String brand;

    private String description;

    private BigDecimal price;

    private MaterialType type;

    private Material(UUID id, String name, String brand, String description, BigDecimal price, MaterialType type) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.type = type;
        validate();
    }

    public static Material create(String name, String brand, String description, BigDecimal price, MaterialType type) {
        var material = new Material(UUID.randomUUID(), name, brand, description, price, type);
        material.create();
        return material;
    }

    public void update(String name, String brand, String description, BigDecimal price, MaterialType type) {
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.type = type;
        update();
        validate();
    }

    @SuppressWarnings("java:S107")
    public static Material restore(UUID id, String name, String brand, String description, BigDecimal price, MaterialType type,
                                    LocalDateTime deletedAt, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        Material material = new Material(id, name, brand, description, price, type);
        material.deletedAt = deletedAt;
        material.dateCreated = dateCreated;
        material.dateUpdated = dateUpdated;
        return material;
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new MaterialExceptions.NameRequired();
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MaterialExceptions.PriceRequired();
        }
        if (type == null) {
            throw new MaterialExceptions.TypeRequired();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MaterialType getType() {
        return type;
    }
}
