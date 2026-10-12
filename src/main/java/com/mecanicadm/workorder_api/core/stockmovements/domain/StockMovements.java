package com.mecanicadm.workorder_api.core.stockmovements.domain;

import com.mecanicadm.workorder_api.core.stockmovements.domain.enums.MovementType;
import com.mecanicadm.workorder_api.shared.domain.AuditDomain;

import java.time.LocalDateTime;
import java.util.UUID;

public class StockMovements extends AuditDomain {

    private final UUID id;

    private final UUID workOrderId;

    private final UUID materialId;

    private final Integer quantity;

    private final MovementType type;

    private StockMovements(UUID id, UUID materialId, UUID workOrderId, Integer quantity, MovementType type) {
        this.id = id;
        this.materialId = materialId;
        this.workOrderId = workOrderId;
        this.quantity = quantity;
        this.type = type;
    }

    public static StockMovements recordAddition(UUID materialId, Integer amount) {
        var movement = new StockMovements(null, materialId, null, amount, MovementType.ADDITION);
        movement.create();
        return movement;
    }

    @SuppressWarnings("java:S107")
    public static StockMovements restore(UUID id, UUID materialId, UUID workOrderId, Integer quantity, MovementType type,
                                          LocalDateTime deletedAt, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        StockMovements movement = new StockMovements(id, materialId, workOrderId, quantity, type);
        movement.deletedAt = deletedAt;
        movement.dateCreated = dateCreated;
        movement.dateUpdated = dateUpdated;
        return movement;
    }

    public static StockMovements recordReduction(UUID materialId, UUID workOrderId, Integer amount) {
        var movement = new StockMovements(null, materialId, workOrderId, amount, MovementType.REDUCTION);
        movement.create();
        return movement;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorkOrderId() {
        return workOrderId;
    }

    public UUID getMaterialId() {
        return materialId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public MovementType getType() {
        return type;
    }
}
