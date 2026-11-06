package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkOrderLaborItemResponseTest {

    @Test
    @DisplayName("Deve retornar 0 quando data de inicio da execucao for nula")
    void shouldReturnZeroWhenExecutionStartAtIsNull() {
        WorkOrderLaborItem laborItem = WorkOrderLaborItem.create(UUID.randomUUID(), UUID.randomUUID());

        ReflectionTestUtils.setField(laborItem, "executionEndAt", LocalDateTime.of(2026, 4, 30, 10, 0));

        WorkOrderLaborItemResponse response = WorkOrderLaborItemResponse.from(laborItem);

        assertEquals(0L, response.totalExecutionTimeInMinutes());
    }

    @Test
    @DisplayName("Deve retornar 0 quando data de fim da execucao for nula")
    void shouldReturnZeroWhenExecutionEndAtIsNull() {
        WorkOrderLaborItem laborItem = WorkOrderLaborItem.create(UUID.randomUUID(), UUID.randomUUID());

        ReflectionTestUtils.setField(laborItem, "executionStartAt", LocalDateTime.of(2026, 4, 30, 9, 0));

        WorkOrderLaborItemResponse response = WorkOrderLaborItemResponse.from(laborItem);

        assertEquals(0L, response.totalExecutionTimeInMinutes());
    }

    @Test
    @DisplayName("Deve calcular total de minutos quando datas de inicio e fim existem")
    void shouldCalculateTotalExecutionTimeWhenExecutionStartAtAndExecutionEndAtArePresent() {
        WorkOrderLaborItem laborItem = WorkOrderLaborItem.create(UUID.randomUUID(), UUID.randomUUID());
        LocalDateTime start = LocalDateTime.of(2026, 4, 30, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 30, 10, 30);

        ReflectionTestUtils.setField(laborItem, "executionStartAt", start);
        ReflectionTestUtils.setField(laborItem, "executionEndAt", end);

        WorkOrderLaborItemResponse response = WorkOrderLaborItemResponse.from(laborItem);

        assertEquals(90L, response.totalExecutionTimeInMinutes());
    }
}

