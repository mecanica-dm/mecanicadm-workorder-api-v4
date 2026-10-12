package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkOrderResponseTest {

    @Test
    @DisplayName("Deve retornar 0 quando datas de execucao forem nulas")
    void shouldReturnZeroWhenExecutionDatesAreNull() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Troca de oleo");

        WorkOrderResponse response = WorkOrderResponse.from(workOrder);

        assertEquals(0L, response.totalExecutionTimeInMinutes());
    }

    @Test
    @DisplayName("Deve retornar 0 quando apenas uma data de execucao existir")
    void shouldReturnZeroWhenOnlyOneExecutionDateExists() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Troca de oleo");

        ReflectionTestUtils.setField(workOrder, "executionStartAt", LocalDateTime.of(2026, 4, 30, 9, 0));

        WorkOrderResponse response = WorkOrderResponse.from(workOrder);

        assertEquals(0L, response.totalExecutionTimeInMinutes());
    }

    @Test
    @DisplayName("Deve calcular total de minutos quando inicio e fim da execucao existirem")
    void shouldCalculateTotalExecutionTimeWhenExecutionDatesArePresent() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Troca de oleo");
        LocalDateTime start = LocalDateTime.of(2026, 4, 30, 8, 15);
        LocalDateTime end = LocalDateTime.of(2026, 4, 30, 9, 0);

        ReflectionTestUtils.setField(workOrder, "executionStartAt", start);
        ReflectionTestUtils.setField(workOrder, "executionEndAt", end);

        WorkOrderResponse response = WorkOrderResponse.from(workOrder);

        assertEquals(45L, response.totalExecutionTimeInMinutes());
    }
}

