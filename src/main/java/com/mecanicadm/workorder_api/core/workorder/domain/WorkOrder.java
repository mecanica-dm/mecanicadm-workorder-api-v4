package com.mecanicadm.workorder_api.core.workorder.domain;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.LaborExecutionStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.shared.domain.AuditDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

public class WorkOrder extends AuditDomain {

    private static final Logger log = LoggerFactory.getLogger(WorkOrder.class);

    private UUID id;

    private UUID clientId;

    private String vehicleId;

    private String description;

    private WorkOrderStatus status;

    private LocalDateTime executionStartAt;

    private LocalDateTime executionEndAt;

    private Set<WorkOrderLaborItem> laborItems = new HashSet<>();

    private Set<WorkOrderMaterialItem> materialItems = new HashSet<>();

    private WorkOrder(UUID id, UUID clientId, String vehicleId, String description, WorkOrderStatus status) {
        this.id = id;
        this.clientId = clientId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.status = status;
        validate();
    }

    private WorkOrder(UUID id, UUID clientId, String vehicleId, String description, WorkOrderStatus status,
                      LocalDateTime executionStartAt, LocalDateTime executionEndAt) {
        this(id, clientId, vehicleId, description, status);
        this.executionStartAt = executionStartAt;
        this.executionEndAt = executionEndAt;
    }

    public static WorkOrder create(UUID clientId, String vehicleId, String description) {
        WorkOrder workOrder = new WorkOrder(UUID.randomUUID(), clientId, vehicleId, description, WorkOrderStatus.RECEIVED);
        workOrder.create();
        log.info("{\"message\": \"OS Criada\", \"workOrderId\": \"{}\", \"status\": \"{}\"}", workOrder.getId(), workOrder.getStatus());
        return workOrder;
    }

    @SuppressWarnings("java:S107")
    public static WorkOrder restore(UUID id, UUID clientId, String vehicleId, String description, WorkOrderStatus status, LocalDateTime executionStartAt, LocalDateTime executionEndAt, Set<WorkOrderLaborItem> laborItems, Set<WorkOrderMaterialItem> materialItems, LocalDateTime dateCreated, LocalDateTime dateUpdated, LocalDateTime deletedAt) {
        WorkOrder workOrder = new WorkOrder(id, clientId, vehicleId, description, status, executionStartAt, executionEndAt);
        workOrder.laborItems = new HashSet<>(laborItems);
        workOrder.materialItems = new HashSet<>(materialItems);
        workOrder.dateCreated = dateCreated;
        workOrder.dateUpdated = dateUpdated;
        workOrder.deletedAt = deletedAt;
        return workOrder;
    }

    public void update(UUID clientId, String vehicleId, String description) {
        if (this.status != WorkOrderStatus.RECEIVED) {
            throw new WorkOrderExceptions.InvalidStatus(this.status.name());
        }

        if (nonNull(clientId)) {
            this.clientId = clientId;
        }

        if (hasText(vehicleId)) {
            this.vehicleId = vehicleId;
        }

        if (nonNull(description)) {
            this.description = description;
        }

        update();
        validate();
    }

    private void validate() {
        if (clientId == null) {
            throw new WorkOrderExceptions.ClientRequired();
        }
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new WorkOrderExceptions.VehicleRequired();
        }
    }

    public void markAsDiagnosed() {
        if (this.status != WorkOrderStatus.RECEIVED) {
            throw new WorkOrderExceptions.InvalidStatusTransition(this.status.name(), WorkOrderStatus.DIAGNOSED.name());
        }
        this.status = WorkOrderStatus.DIAGNOSED;
        log.info("{\"message\": \"OS Diagnosticada\", \"workOrderId\": \"{}\", \"status\": \"{}\"}", this.id, this.status);
    }

    public void markAsAwaitingExecution() {
        this.status = WorkOrderStatus.AWAITING_EXECUTION;
    }

    public void markAsInExecution() {
        if (this.status != WorkOrderStatus.AWAITING_EXECUTION) {
            throw new WorkOrderExceptions.InvalidStatusTransition(this.status.name(), WorkOrderStatus.IN_EXECUTION.name());
        }
        this.status = WorkOrderStatus.IN_EXECUTION;
        this.executionStartAt = LocalDateTime.now();
        log.info("{\"message\": \"OS Em execução\",  \"workOrderId\": \"{}\", \"status\": \"{}\"}", this.id, this.status);
    }

    public void startLaborItem(UUID laborItemId) {
        if (this.status != WorkOrderStatus.IN_EXECUTION) {
            throw new WorkOrderExceptions.LaborCannotStartIfNotInExecution();
        }
        findLaborItem(laborItemId)
                .orElseThrow(WorkOrderExceptions.LaborItemNotFound::new)
                .startExecution();
    }

    public void markAsExecutionCompleted() {
        if (this.status != WorkOrderStatus.IN_EXECUTION) {
            throw new WorkOrderExceptions.InvalidStatusTransition(this.status.name(), WorkOrderStatus.EXECUTION_COMPLETED.name());
        }

        boolean hasLaborAwaitingExecution = laborItems.stream()
                .anyMatch(item -> item.getStatus() != LaborExecutionStatus.EXECUTION_COMPLETED);

        if (hasLaborAwaitingExecution) {
            throw new WorkOrderExceptions.PendingLaborItems();
        }

        this.status = WorkOrderStatus.EXECUTION_COMPLETED;
        this.executionEndAt = LocalDateTime.now();
        log.info("{\"message\": \"OS Finalizada\",  \"workOrderId\": \"{}\", \"status\": \"{}\"}", this.id, this.status);
    }

    public void markAsPaid() {
        if (this.status != WorkOrderStatus.EXECUTION_COMPLETED) {
            throw new WorkOrderExceptions.InvalidStatusTransition(this.status.name(), WorkOrderStatus.PAID.name());
        }
        this.status = WorkOrderStatus.PAID;
    }

    public void markAsDelivered() {
        if (this.status != WorkOrderStatus.PAID) {
            throw new WorkOrderExceptions.InvalidStatusTransition(this.status.name(), WorkOrderStatus.DELIVERED.name());
        }
        this.status = WorkOrderStatus.DELIVERED;
    }

    public Optional<WorkOrderLaborItem> findLaborItem(UUID laborItemId) {
        return this.laborItems.stream()
                .filter(item -> item.getId().equals(laborItemId))
                .findFirst();
    }

    public void markAsChangesRequested() {
        this.status = WorkOrderStatus.CHANGES_REQUESTED;
    }

    public void cancel() {
        this.status = WorkOrderStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public Optional<LocalDateTime> getExecutionStartAt() {
        return Optional.ofNullable(executionStartAt);
    }

    public Optional<LocalDateTime> getExecutionEndAt() {
        return Optional.ofNullable(executionEndAt);
    }

    public Set<WorkOrderLaborItem> getLaborItems() {
        return Collections.unmodifiableSet(laborItems);
    }

    public Set<WorkOrderMaterialItem> getMaterialItems() {
        return Collections.unmodifiableSet(materialItems);
    }
}
