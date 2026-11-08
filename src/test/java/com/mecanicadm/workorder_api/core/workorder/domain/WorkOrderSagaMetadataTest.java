package com.mecanicadm.workorder_api.core.workorder.domain;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorkOrderSagaMetadataTest {

    private WorkOrder newWorkOrder() {
        return WorkOrder.create(UUID.randomUUID(), "VEH-001", "Troca de oleo");
    }

    @Test
    @DisplayName("Deve iniciar sem alteracoes solicitadas ao criar ordem de servico")
    void shouldStartWithoutRequestedChangesWhenCreatingWorkOrder() {
        assertNull(newWorkOrder().getRequestedChanges());
    }

    @Test
    @DisplayName("Deve guardar as alteracoes solicitadas ao marcar a OS como alteracoes solicitadas")
    void shouldStoreRequestedChangesWhenChangesAreRequested() {
        WorkOrder workOrder = newWorkOrder();

        workOrder.markAsChangesRequested("Nao trocar filtro do ar condicionado");

        assertEquals(WorkOrderStatus.CHANGES_REQUESTED, workOrder.getStatus());
        assertEquals("Nao trocar filtro do ar condicionado", workOrder.getRequestedChanges());
    }

    @Test
    @DisplayName("Deve manter somente a ultima solicitacao de alteracoes")
    void shouldKeepOnlyLatestRequestedChanges() {
        WorkOrder workOrder = newWorkOrder();

        workOrder.markAsChangesRequested("primeira");
        workOrder.markAsDiagnosed();
        workOrder.markAsChangesRequested("segunda");

        assertEquals("segunda", workOrder.getRequestedChanges());
    }

    @Test
    @DisplayName("Deve restaurar as alteracoes solicitadas a partir da persistencia")
    void shouldRestoreRequestedChangesWhenRestoringWorkOrder() {
        WorkOrder workOrder = WorkOrder.restore(UUID.randomUUID(), UUID.randomUUID(), "VEH-001", "desc",
                WorkOrderStatus.CHANGES_REQUESTED, null, null, new HashSet<>(), new HashSet<>(), null, null, null, "ajustar");

        assertEquals("ajustar", workOrder.getRequestedChanges());
    }
}
