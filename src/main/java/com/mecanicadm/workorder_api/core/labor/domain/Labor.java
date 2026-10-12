package com.mecanicadm.workorder_api.core.labor.domain;

import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.shared.domain.AuditDomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Labor extends AuditDomain {

    private final UUID id;
    private String name;
    private BigDecimal price;

    private Labor(UUID id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
        validate();
    }

    public static Labor create(String name, BigDecimal price) {
        var labor = new Labor(UUID.randomUUID(), name, price);
        labor.create();
        return labor;
    }

    @SuppressWarnings("java:S107")
    public static Labor restore(UUID id, String name, BigDecimal price, LocalDateTime deletedAt, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        Labor labor = new Labor(id, name, price);
        labor.deletedAt = deletedAt;
        labor.dateCreated = dateCreated;
        labor.dateUpdated = dateUpdated;
        return labor;
    }

    public void update(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
        update();
        validate();
    }

    public void softDelete() {
        delete();
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new LaborExceptions.NameRequired();
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new LaborExceptions.PriceRequired();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
