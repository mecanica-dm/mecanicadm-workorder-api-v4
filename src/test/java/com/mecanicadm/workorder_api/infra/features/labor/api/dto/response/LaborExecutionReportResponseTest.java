package com.mecanicadm.workorder_api.infra.features.labor.api.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LaborExecutionReportResponseTest {

    @Test
    @DisplayName("Deve criar LaborExecutionReportResponse via construtor")
    void shouldCreateLaborExecutionReportResponse() {
        var stats = new LaborTypeExecutionStatsResponse(
                UUID.randomUUID(),
                "Troca de Óleo",
                5L,
                30.0,
                15.0,
                45.0
        );
        var response = new LaborExecutionReportResponse(
                "minutes",
                10L,
                25.5,
                List.of(stats)
        );

        assertNotNull(response);
        assertEquals("minutes", response.durationUnit());
        assertEquals(10L, response.totalProcessedLabors());
        assertEquals(25.5, response.averageLaborExecutionTime());
        assertEquals(1, response.statsByLaborType().size());
        assertEquals(stats, response.statsByLaborType().getFirst());
    }
}
