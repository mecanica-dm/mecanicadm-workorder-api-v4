package com.mecanicadm.workorder_api.core.labor.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LaborExecutionReportTest {

    @Test
    @DisplayName("Deve criar relatório de execução com todos os valores")
    void shouldCreateLaborExecutionReportWithAllValues() {
        var stats = new LaborTypeExecutionStats(
                java.util.UUID.randomUUID(),
                "Troca de Óleo",
                5L,
                30.0,
                15.0,
                45.0
        );
        var report = new LaborExecutionReport(
                "minutes",
                10L,
                25.5,
                List.of(stats)
        );

        assertNotNull(report);
        assertEquals("minutes", report.durationUnit());
        assertEquals(10L, report.totalProcessedLabors());
        assertEquals(25.5, report.averageLaborExecutionTime());
        assertEquals(1, report.statsByLaborType().size());
        assertEquals(stats, report.statsByLaborType().getFirst());
    }
}
