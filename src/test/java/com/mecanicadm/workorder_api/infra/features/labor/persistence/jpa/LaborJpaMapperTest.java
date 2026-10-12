package com.mecanicadm.workorder_api.infra.features.labor.persistence.jpa;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.infra.features.labor.persistence.entity.LaborJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LaborJpaMapperTest {

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio")
    void shouldMapToDomain() {
        var id = UUID.randomUUID();
        var now = LocalDateTime.now();
        var entity = new LaborJpaEntity(id, "Troca de Óleo", new BigDecimal("150.00"));
        entity.setDateCreated(now);
        entity.setDateUpdated(now);
        entity.setDeletedAt(null);

        var domain = LaborJpaMapper.toDomain(entity);

        assertEquals(id, domain.getId());
        assertEquals("Troca de Óleo", domain.getName());
        assertEquals(new BigDecimal("150.00"), domain.getPrice());
        assertEquals(now, domain.getDateCreated());
        assertEquals(now, domain.getDateUpdated());
    }

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity")
    void shouldMapToEntity() {
        var labor = Labor.create("Troca de Óleo", new BigDecimal("150.00"));

        var entity = LaborJpaMapper.toEntity(labor);

        assertEquals(labor.getId(), entity.getId());
        assertEquals("Troca de Óleo", entity.getName());
        assertEquals(new BigDecimal("150.00"), entity.getPrice());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
    }
}
