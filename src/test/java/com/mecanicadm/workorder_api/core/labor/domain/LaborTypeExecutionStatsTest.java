package com.mecanicadm.workorder_api.core.labor.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LaborTypeExecutionStatsTest {

    @Test
    @DisplayName("Deve criar estatísticas de execução por tipo de serviço com todos os valores")
    void shouldCreateLaborTypeExecutionStatsWithAllValues() {
        var laborId = UUID.randomUUID();
        var stats = new LaborTypeExecutionStats(
                laborId,
                "Troca de Óleo",
                10L,
                45.0,
                20.0,
                90.0
        );

        assertNotNull(stats);
        assertEquals(laborId, stats.laborId());
        assertEquals("Troca de Óleo", stats.laborName());
        assertEquals(10L, stats.totalExecutions());
        assertEquals(45.0, stats.averageExecutionTimeInMinutes());
        assertEquals(20.0, stats.shortestExecutionTimeInMinutes());
        assertEquals(90.0, stats.longestExecutionTimeInMinutes());
    }
}
