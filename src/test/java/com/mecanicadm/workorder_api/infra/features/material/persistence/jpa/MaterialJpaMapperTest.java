package com.mecanicadm.workorder_api.infra.features.material.persistence.jpa;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.infra.features.material.persistence.entity.MaterialJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaterialJpaMapperTest {

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio")
    void shouldMapToDomain() {
        var id = UUID.randomUUID();
        var now = LocalDateTime.now();
        var entity = new MaterialJpaEntity(id, "Óleo 5W30", "Castrol", "Óleo sintético",
                new BigDecimal("65.00"), MaterialType.CONSUMABLE);
        entity.setDateCreated(now);
        entity.setDateUpdated(now);
        entity.setDeletedAt(null);

        var domain = MaterialJpaMapper.toDomain(entity);

        assertEquals(id, domain.getId());
        assertEquals("Óleo 5W30", domain.getName());
        assertEquals("Castrol", domain.getBrand());
        assertEquals("Óleo sintético", domain.getDescription());
        assertEquals(new BigDecimal("65.00"), domain.getPrice());
        assertEquals(MaterialType.CONSUMABLE, domain.getType());
        assertEquals(now, domain.getDateCreated());
        assertEquals(now, domain.getDateUpdated());
    }

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity")
    void shouldMapToEntity() {
        var material = Material.create("Óleo 5W30", "Castrol", "Óleo sintético",
                new BigDecimal("65.00"), MaterialType.CONSUMABLE);

        var entity = MaterialJpaMapper.toEntity(material);

        assertEquals(material.getId(), entity.getId());
        assertEquals("Óleo 5W30", entity.getName());
        assertEquals("Castrol", entity.getBrand());
        assertEquals("Óleo sintético", entity.getDescription());
        assertEquals(new BigDecimal("65.00"), entity.getPrice());
        assertEquals(MaterialType.CONSUMABLE, entity.getType());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
    }
}
