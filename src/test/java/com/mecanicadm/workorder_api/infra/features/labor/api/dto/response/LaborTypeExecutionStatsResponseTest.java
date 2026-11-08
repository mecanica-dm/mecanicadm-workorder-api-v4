package com.mecanicadm.workorder_api.infra.features.labor.api.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LaborTypeExecutionStatsResponseTest {

    @Test
    @DisplayName("Deve criar LaborTypeExecutionStatsResponse via construtor")
    void shouldCreateLaborTypeExecutionStatsResponse() {
        var laborId = UUID.randomUUID();
        var response = new LaborTypeExecutionStatsResponse(
                laborId,
                "Troca de Óleo",
                10L,
                45.0,
                20.0,
                90.0
        );

        assertNotNull(response);
        assertEquals(laborId, response.laborId());
        assertEquals("Troca de Óleo", response.laborName());
        assertEquals(10L, response.totalExecutions());
        assertEquals(45.0, response.averageExecutionTimeInMinutes());
        assertEquals(20.0, response.shortestExecutionTimeInMinutes());
        assertEquals(90.0, response.longestExecutionTimeInMinutes());
    }
}
