package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

final class SagaCommandUseCaseTestSupport {
    private SagaCommandUseCaseTestSupport() {
    }

    static WorkOrder workOrderIn(String step) {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        if (step.equals("RECEIVED")) {
            return workOrder;
        }
        workOrder.markAsDiagnosed();
        if (step.equals("DIAGNOSED")) {
            return workOrder;
        }
        workOrder.markAsAwaitingExecution();
        if (step.equals("AWAITING_EXECUTION")) {
            return workOrder;
        }
        workOrder.markAsInExecution();
        if (step.equals("IN_EXECUTION")) {
            return workOrder;
        }
        workOrder.markAsExecutionCompleted();
        if (step.equals("EXECUTION_COMPLETED")) {
            return workOrder;
        }
        workOrder.releaseForDelivery();
        if (step.equals("PAID")) {
            return workOrder;
        }
        workOrder.markAsDelivered();
        return workOrder;
    }

    static void givenStored(WorkOrderGateway gateway, WorkOrder workOrder) {
        when(gateway.findById(workOrder.getId())).thenReturn(Optional.of(workOrder));
    }
}
