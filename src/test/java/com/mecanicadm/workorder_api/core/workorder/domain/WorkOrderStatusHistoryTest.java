package com.mecanicadm.workorder_api.core.workorder.domain;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkOrderStatusHistoryTest {

    private WorkOrder newWorkOrder() {
        return WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Revisao");
    }

    private List<WorkOrderStatus> targets(List<WorkOrderStatusChange> changes) {
        return changes.stream().map(WorkOrderStatusChange::toStatus).toList();
    }

    @Test
    @DisplayName("Deve registrar a criacao da OS como mudanca de nada para RECEIVED")
    void shouldRecordCreation() {
        List<WorkOrderStatusChange> changes = newWorkOrder().pullStatusChanges();

        assertEquals(1, changes.size());
        assertNull(changes.get(0).fromStatus());
        assertEquals(WorkOrderStatus.RECEIVED, changes.get(0).toStatus());
        assertNotNull(changes.get(0).changedAt());
        assertNull(changes.get(0).changedBy());
    }

    @Test
    @DisplayName("Deve registrar cada transicao do ciclo de vida, com o status anterior e o novo")
    void shouldRecordEveryTransitionOfTheLifecycle() {
        WorkOrder workOrder = newWorkOrder();
        workOrder.pullStatusChanges();

        workOrder.markAsDiagnosed();
        workOrder.markAsAwaitingExecution();
        workOrder.markAsInExecution();
        workOrder.markAsExecutionCompleted();
        workOrder.releaseForDelivery();
        workOrder.markAsDelivered();

        List<WorkOrderStatusChange> changes = workOrder.pullStatusChanges();
        assertEquals(List.of(WorkOrderStatus.DIAGNOSED, WorkOrderStatus.AWAITING_EXECUTION, WorkOrderStatus.IN_EXECUTION,
                WorkOrderStatus.EXECUTION_COMPLETED, WorkOrderStatus.PAID, WorkOrderStatus.DELIVERED), targets(changes));
        assertEquals(WorkOrderStatus.RECEIVED, changes.get(0).fromStatus());
        assertEquals(WorkOrderStatus.PAID, changes.get(5).fromStatus());
    }

    @Test
    @DisplayName("Deve registrar o pedido de alteracoes com a observacao como motivo")
    void shouldRecordChangesRequestedWithReason() {
        WorkOrder workOrder = newWorkOrder();
        workOrder.pullStatusChanges();

        workOrder.markAsChangesRequested("Trocar o filtro de ar");

        WorkOrderStatusChange change = workOrder.pullStatusChanges().get(0);
        assertEquals(WorkOrderStatus.CHANGES_REQUESTED, change.toStatus());
        assertEquals("Trocar o filtro de ar", change.reason());
    }

    @Test
    @DisplayName("Deve registrar o cancelamento e nao repetir o registro ao cancelar de novo")
    void shouldRecordCancellationOnce() {
        WorkOrder workOrder = newWorkOrder();
        workOrder.pullStatusChanges();

        workOrder.cancel();
        workOrder.cancel();

        assertEquals(List.of(WorkOrderStatus.CANCELLED), targets(workOrder.pullStatusChanges()));
    }

    @Test
    @DisplayName("Nao deve registrar mudanca quando a transicao e recusada")
    void shouldNotRecordWhenTransitionIsRefused() {
        WorkOrder workOrder = newWorkOrder();
        workOrder.pullStatusChanges();

        try {
            workOrder.markAsInExecution();
        } catch (RuntimeException expected) {
            // transicao invalida a partir de RECEIVED
        }

        assertTrue(workOrder.pullStatusChanges().isEmpty());
    }

    @Test
    @DisplayName("Deve devolver as mudancas uma unica vez: depois de pegas, a lista fica vazia")
    void shouldPullChangesOnlyOnce() {
        WorkOrder workOrder = newWorkOrder();

        assertEquals(1, workOrder.pullStatusChanges().size());
        assertTrue(workOrder.pullStatusChanges().isEmpty());
    }

    @Test
    @DisplayName("Uma OS restaurada do banco nao tem mudancas pendentes")
    void restoredWorkOrderHasNoPendingChanges() {
        WorkOrder restored = WorkOrder.restore(UUID.randomUUID(), UUID.randomUUID(), "ABC-1234", "Revisao", WorkOrderStatus.DIAGNOSED,
                null, null, new HashSet<>(), new HashSet<>(), LocalDateTime.now(), LocalDateTime.now(), null);

        assertTrue(restored.pullStatusChanges().isEmpty());
        restored.markAsAwaitingExecution();
        assertEquals(WorkOrderStatus.DIAGNOSED, restored.pullStatusChanges().get(0).fromStatus());
    }

    @Test
    @DisplayName("withChangedBy deve devolver uma copia com o autor preenchido")
    void shouldFillTheActor() {
        WorkOrderStatusChange change = new WorkOrderStatusChange(null, WorkOrderStatus.RECEIVED, null, null, LocalDateTime.now());

        assertEquals("maria@teste.com", change.withChangedBy("maria@teste.com").changedBy());
        assertNull(change.changedBy());
    }
}
