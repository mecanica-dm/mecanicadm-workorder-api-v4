package com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.entity;

import com.mecanicadm.workorder_api.core.stockmovements.domain.enums.MovementType;
import com.mecanicadm.workorder_api.infra.audit.AuditEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@SQLRestriction("deleted_at IS NULL")
public class StockMovementsJpaEntity extends AuditEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "work_order_id")
    private UUID workOrderId;

    @Column(name = "material_id", nullable = false)
    private UUID materialId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MovementType type;

    public StockMovementsJpaEntity() {
    }

    public StockMovementsJpaEntity(UUID id, UUID materialId, UUID workOrderId, Integer quantity, MovementType type) {
        this.id = id;
        this.materialId = materialId;
        this.workOrderId = workOrderId;
        this.quantity = quantity;
        this.type = type;
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
