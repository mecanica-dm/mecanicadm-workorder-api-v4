package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorkOrderBudgetResponseTest {

    @Test
    @DisplayName("Deve criar WorkOrderBudgetResponse a partir de WorkOrderBudget via factory method")
    void shouldCreateBudgetResponseFromBudget() {
        var workOrderId = UUID.randomUUID();
        var budget = WorkOrderBudget.restore(workOrderId, new BigDecimal("350.00"), WorkOrderBudgetStatus.APPROVED, null);

        WorkOrderBudgetResponse response = WorkOrderBudgetResponse.from(budget);

        assertEquals(new BigDecimal("350.00"), response.totalPrice());
        assertEquals(WorkOrderBudgetStatus.APPROVED, response.status());
        assertNull(response.observation());
    }

    @Test
    @DisplayName("Deve criar WorkOrderBudgetResponse com observation")
    void shouldCreateBudgetResponseWithObservation() {
        var workOrderId = UUID.randomUUID();
        var budget = WorkOrderBudget.restore(workOrderId, new BigDecimal("200.00"), WorkOrderBudgetStatus.REJECTED, "Cliente recusou");

        WorkOrderBudgetResponse response = WorkOrderBudgetResponse.from(budget);

        assertEquals(new BigDecimal("200.00"), response.totalPrice());
        assertEquals(WorkOrderBudgetStatus.REJECTED, response.status());
        assertEquals("Cliente recusou", response.observation());
    }
}
