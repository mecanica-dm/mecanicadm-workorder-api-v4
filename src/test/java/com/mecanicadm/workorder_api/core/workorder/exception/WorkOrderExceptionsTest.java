package com.mecanicadm.workorder_api.core.workorder.exception;

import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WorkOrderExceptionsTest {

    @Test
    @DisplayName("Deve cobrir o construtor privado de WorkOrderExceptions")
    void shouldCoverPrivateConstructor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<WorkOrderExceptions> constructor = WorkOrderExceptions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        WorkOrderExceptions instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    @DisplayName("Deve instanciar todas as exceções de WorkOrderJpaEntity para garantir cobertura")
    void shouldInstantiateAllExceptions() {
        assertException(new WorkOrderExceptions.NotFound(), 404, "work.order.not.found");
        assertException(new WorkOrderExceptions.InvalidMaterialQuantity(), 400, "work.order.material.quantity.invalid");
        assertException(new WorkOrderExceptions.InvalidStatusTransition("FROM", "TO"), 400, "work.order.status.transition.invalid");
        assertException(new WorkOrderExceptions.LaborItemsRequired(), 400, "work.order.labor.required");
        assertException(new WorkOrderExceptions.LaborItemNotFound(), 404, "work.order.labor.item.not.found");
        assertException(new WorkOrderExceptions.ClientRequired(), 400, "work.order.client.required");
        assertException(new WorkOrderExceptions.VehicleRequired(), 400, "work.order.vehicle.required");
        assertException(new WorkOrderExceptions.BudgetNotFound(), 404, "work.order.budget.not.found");
        assertException(new WorkOrderExceptions.BudgetDecisionInvalid("INVALID_DECISION"), 400, "work.order.budget.decision.invalid");
        assertException(new WorkOrderExceptions.BudgetObservationRequired(), 400, "work.order.budget.observation.required");
        assertException(new WorkOrderExceptions.BudgetNotWaitingDecision(), 400, "work.order.budget.not.waiting.decision");
        assertException(new WorkOrderExceptions.InvalidLaborStatusTransition("FROM", "TO"), 400, "work.order.labor.status.transition.invalid");
        assertException(new WorkOrderExceptions.LaborCannotStartIfNotInExecution(), 400, "work.order.labor.start.not.in.execution");
        assertException(new WorkOrderExceptions.InvalidReportPeriod(), 400, "work.order.report.period.invalid");
    }

    private void assertException(DomainExceptionCore ex, int status, String messageKey) {
        assertEquals(status, ex.getStatus());
        assertEquals(messageKey, ex.getMessageKey());
    }
}
