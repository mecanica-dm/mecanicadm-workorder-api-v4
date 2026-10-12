package com.mecanicadm.workorder_api.core.workorder.domain;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderTest {

    @Test
    @DisplayName("Deve criar ordem de servico com status inicial RECEIVED")
    void shouldCreateWorkOrderWithReceivedStatus() {
        UUID clientId = UUID.randomUUID();
        WorkOrder workOrder = WorkOrder.create(clientId, "VEH-001", "Troca de oleo");

        assertNotNull(workOrder.getId());
        assertEquals(clientId, workOrder.getClientId());
        assertEquals("VEH-001", workOrder.getVehicleId());
        assertEquals("Troca de oleo", workOrder.getDescription());
        assertEquals(WorkOrderStatus.RECEIVED, workOrder.getStatus());
        assertTrue(workOrder.getBudget().isEmpty());
        assertTrue(workOrder.getExecutionStartAt().isEmpty());
        assertTrue(workOrder.getExecutionEndAt().isEmpty());
        assertTrue(workOrder.getLaborItems().isEmpty());
        assertTrue(workOrder.getMaterialItems().isEmpty());
        assertNull(workOrder.getDeletedAt());
    }

    @Test
    @DisplayName("Nao deve criar ordem de servico com clientId nulo")
    void shouldThrowWhenCreatingWorkOrderWithNullClientId() {
        assertThrows(
                WorkOrderExceptions.ClientRequired.class,
                () -> WorkOrder.create(null, "VEH-001", "Servico")
        );
    }

    @Test
    @DisplayName("Nao deve criar ordem de servico com vehicleId nulo")
    void shouldThrowWhenCreatingWorkOrderWithNullVehicleId() {
        UUID id = UUID.randomUUID();

        assertThrows(
                WorkOrderExceptions.VehicleRequired.class,
                () -> WorkOrder.create(id, null, "Servico")
        );
    }

    @Test
    @DisplayName("Deve retornar colecao imutavel para itens de mao de obra")
    void shouldReturnUnmodifiableLaborItemsWhenAccessingLaborItems() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");

        UUID id = UUID.randomUUID();
        WorkOrderLaborItem item = WorkOrderLaborItem.create(id, UUID.randomUUID());

        Set<WorkOrderLaborItem> laborItems = workOrder.getLaborItems();

        assertThrows(
                UnsupportedOperationException.class,
                () -> laborItems.add(item)
        );
    }

    @Test
    @DisplayName("Deve retornar colecao imutavel para itens de material")
    void shouldReturnUnmodifiableMaterialItemsWhenAccessingMaterialItems() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        UUID id = UUID.randomUUID();
        WorkOrderMaterialItem item = WorkOrderMaterialItem.create(id, 1);

        Set<WorkOrderMaterialItem> materialItems = workOrder.getMaterialItems();

        assertThrows(
                UnsupportedOperationException.class,
                () -> materialItems.add(item)
        );
    }

    @Test
    @DisplayName("Deve alterar status para DIAGNOSED quando ordem estiver RECEIVED")
    void shouldMarkAsDiagnosedWhenStatusIsReceived() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");

        workOrder.markAsDiagnosed();

        assertEquals(WorkOrderStatus.DIAGNOSED, workOrder.getStatus());
    }

    @Test
    @DisplayName("Deve marcar como aguardando execucao")
    void shouldMarkAsAwaitingExecution() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.markAsAwaitingExecution();
        assertEquals(WorkOrderStatus.AWAITING_EXECUTION, workOrder.getStatus());
    }

    @Test
    @DisplayName("Deve marcar como em execucao a partir de aguardando execucao")
    void shouldMarkAsInExecution() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.markAsAwaitingExecution();

        workOrder.markAsInExecution();

        assertEquals(WorkOrderStatus.IN_EXECUTION, workOrder.getStatus());
        assertTrue(workOrder.getExecutionStartAt().isPresent());
    }

    @Test
    @DisplayName("Deve lancar excecao ao marcar como em execucao a partir de status invalido")
    void shouldThrowExceptionWhenMarkingAsInExecutionWithInvalidStatus() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");

        DomainExceptionCore exception = assertThrows(DomainExceptionCore.class, workOrder::markAsInExecution);

        assertAll(
                () -> assertEquals(WorkOrderExceptions.InvalidStatusTransition.class, exception.getClass()),
                () -> assertEquals("work.order.status.transition.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
    }

    @Test
    @DisplayName("Deve marcar como execucao completada")
    void shouldMarkAsExecutionCompleted() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.markAsAwaitingExecution();
        workOrder.markAsInExecution();

        workOrder.markAsExecutionCompleted();

        assertEquals(WorkOrderStatus.EXECUTION_COMPLETED, workOrder.getStatus());
        assertTrue(workOrder.getExecutionEndAt().isPresent());
    }

    @Test
    @DisplayName("Deve lancar excecao ao marcar como completada sem estar em execucao")
    void shouldThrowExceptionWhenMarkingAsCompletedWithInvalidStatus() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.markAsAwaitingExecution();

        DomainExceptionCore exception = assertThrows(DomainExceptionCore.class, workOrder::markAsExecutionCompleted);

        assertAll(
                () -> assertEquals(WorkOrderExceptions.InvalidStatusTransition.class, exception.getClass()),
                () -> assertEquals("work.order.status.transition.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
    }

    @Test
    @DisplayName("Deve lancar excecao ao marcar como completada com itens de mao de obra pendentes")
    void shouldThrowExceptionWhenMarkingAsCompletedWithPendingLaborItems() {
        UUID id = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        WorkOrderLaborItem laborItem = WorkOrderLaborItem.create(UUID.randomUUID(), id);
        WorkOrder workOrder = WorkOrder.restore(id, clientId, "VEH-001", "Servico",
                WorkOrderStatus.IN_EXECUTION, LocalDateTime.now(), null,
                Set.of(laborItem), new HashSet<>(), null, null, null, null);

        DomainExceptionCore exception = assertThrows(DomainExceptionCore.class, workOrder::markAsExecutionCompleted);

        assertAll(
                () -> assertEquals(WorkOrderExceptions.PendingLaborItems.class, exception.getClass()),
                () -> assertEquals("work.order.labor.pending.items", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
    }

    @Test
    @DisplayName("Deve marcar como paga")
    void shouldMarkAsPaid() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.markAsAwaitingExecution();
        workOrder.markAsInExecution();
        workOrder.markAsExecutionCompleted();

        workOrder.markAsPaid();

        assertEquals(WorkOrderStatus.PAID, workOrder.getStatus());
    }

    @Test
    @DisplayName("Deve marcar como entregue")
    void shouldMarkAsDelivered() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.markAsAwaitingExecution();
        workOrder.markAsInExecution();
        workOrder.markAsExecutionCompleted();
        workOrder.markAsPaid();

        workOrder.markAsDelivered();

        assertEquals(WorkOrderStatus.DELIVERED, workOrder.getStatus());
    }

    @Test
    @DisplayName("Deve lancar excecao ao marcar como entregue sem estar paga")
    void shouldThrowExceptionWhenMarkingAsDeliveredWithInvalidStatus() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");

        DomainExceptionCore exception = assertThrows(DomainExceptionCore.class, workOrder::markAsDelivered);

        assertAll(
                () -> assertEquals(WorkOrderExceptions.InvalidStatusTransition.class, exception.getClass()),
                () -> assertEquals("work.order.status.transition.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
    }

    @Test
    @DisplayName("Deve lancar excecao ao marcar como paga a partir de status invalido")
    void shouldThrowExceptionWhenMarkingAsPaidWithInvalidStatus() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");

        DomainExceptionCore exception = assertThrows(DomainExceptionCore.class, workOrder::markAsPaid);

        assertAll(
                () -> assertEquals(WorkOrderExceptions.InvalidStatusTransition.class, exception.getClass()),
                () -> assertEquals("work.order.status.transition.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
    }

    @Test
    @DisplayName("Deve marcar como revisao solicitada")
    void shouldMarkAsChangesRequested() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.markAsChangesRequested();
        assertEquals(WorkOrderStatus.CHANGES_REQUESTED, workOrder.getStatus());
    }

    @Test
    @DisplayName("Deve cancelar a ordem de servico")
    void shouldCancelWorkOrder() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.cancel();
        assertEquals(WorkOrderStatus.CANCELLED, workOrder.getStatus());
    }

    @Test
    @DisplayName("Deve lancar excecao ao diagnosticar ordem de servico fora do fluxo permitido")
    void shouldThrowInvalidStatusTransitionWhenDiagnosingAlreadyDiagnosedWorkOrder() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.markAsDiagnosed();

        DomainExceptionCore exception = assertThrows(DomainExceptionCore.class, workOrder::markAsDiagnosed);

        assertAll(
                () -> assertEquals(WorkOrderExceptions.InvalidStatusTransition.class, exception.getClass()),
                () -> assertEquals("work.order.status.transition.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
    }

    @Test
    @DisplayName("Deve atualizar os campos da ordem de serviço")
    void shouldUpdateWorkOrderFields() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Descricao Original");
        UUID newClientId = UUID.randomUUID();
        String newVehicleId = "VEH-999";
        String newDescription = "Descricao Atualizada";

        workOrder.update(newClientId, newVehicleId, newDescription);

        assertAll(
                () -> assertEquals(newClientId, workOrder.getClientId()),
                () -> assertEquals(newVehicleId, workOrder.getVehicleId()),
                () -> assertEquals(newDescription, workOrder.getDescription())
        );
    }

    @Test
    @DisplayName("Nao deve atualizar campos nulos ou vazios")
    void shouldNotUpdateFieldsWhenValuesAreNullOrEmpty() {
        UUID originalClientId = UUID.randomUUID();
        String originalVehicleId = "VEH-001";
        String originalDescription = "Descricao Original";
        WorkOrder workOrder = WorkOrder.create(originalClientId, originalVehicleId, originalDescription);

        workOrder.update(null, null, null);

        assertAll(
                () -> assertEquals(originalClientId, workOrder.getClientId()),
                () -> assertEquals(originalVehicleId, workOrder.getVehicleId()),
                () -> assertEquals(originalDescription, workOrder.getDescription())
        );
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar atualizar ordem de servico que nao esta com status RECEIVED")
    void shouldThrowExceptionWhenUpdatingWorkOrderWithStatusOtherThanReceived() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        workOrder.markAsDiagnosed();

        UUID newId = UUID.randomUUID();

        DomainExceptionCore exception = assertThrows(
                DomainExceptionCore.class,
                () -> workOrder.update(newId, "VEH-999", "Nova Descricao")
        );

        assertAll(
                () -> assertEquals(WorkOrderExceptions.InvalidStatus.class, exception.getClass()),
                () -> assertEquals("work.order.status.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
    }
}
