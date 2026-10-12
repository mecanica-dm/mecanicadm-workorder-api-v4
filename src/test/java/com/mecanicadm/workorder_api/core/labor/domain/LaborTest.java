package com.mecanicadm.workorder_api.core.labor.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LaborTest {

    @Test
    @DisplayName("Deve criar um serviço com sucesso")
    void shouldCreateLaborSuccessfully() {
        Labor labor = Labor.create(
                "Troca de Óleo",
                new BigDecimal("50.00")
        );

        assertNotNull(labor);
        assertEquals("Troca de Óleo", labor.getName());
        assertEquals(new BigDecimal("50.00"), labor.getPrice());
    }

    @Test
    @DisplayName("Deve atualizar as informações do serviço com sucesso")
    void shouldUpdateLaborSuccessfully() {
        Labor labor = Labor.create(
                "Troca de Óleo",
                new BigDecimal("50.00")
        );

        labor.update(
                "Revisão Geral",
                new BigDecimal("150.00")
        );

        assertEquals("Revisão Geral", labor.getName());
        assertEquals(new BigDecimal("150.00"), labor.getPrice());
    }

    @Test
    @DisplayName("Deve restaurar um serviço a partir de dados existentes")
    void shouldRestoreLabor() {
        var id = UUID.randomUUID();
        var now = LocalDateTime.now();
        var labor = Labor.restore(id, "Troca de Óleo", new BigDecimal("50.00"), null, now, now);

        assertEquals(id, labor.getId());
        assertEquals("Troca de Óleo", labor.getName());
        assertEquals(new BigDecimal("50.00"), labor.getPrice());
        assertEquals(now, labor.getDateCreated());
        assertEquals(now, labor.getDateUpdated());
    }

    @Test
    @DisplayName("Deve realizar soft delete")
    void shouldSoftDelete() {
        Labor labor = Labor.create("Troca de Óleo", new BigDecimal("50.00"));

        labor.softDelete();

        assertTrue(labor.isDeleted());
    }
}
