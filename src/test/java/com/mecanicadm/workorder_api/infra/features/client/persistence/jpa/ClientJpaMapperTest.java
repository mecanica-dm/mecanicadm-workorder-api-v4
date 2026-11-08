package com.mecanicadm.workorder_api.infra.features.client.persistence.jpa;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.infra.features.client.persistence.entity.ClientJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClientJpaMapperTest {

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio")
    void shouldMapToDomain() {
        var id = UUID.randomUUID();
        var now = LocalDateTime.now();
        var entity = new ClientJpaEntity(id, "Test", "test@test.com", "12345678901", "48999999999");
        entity.setDateCreated(now);
        entity.setDateUpdated(now);
        entity.setDeletedAt(null);

        var domain = ClientJpaMapper.toDomain(entity);

        assertEquals(id, domain.getId());
        assertEquals("Test", domain.getName());
        assertEquals("test@test.com", domain.getEmail());
        assertEquals("12345678901", domain.getDocument());
        assertEquals("48999999999", domain.getPhone());
        assertEquals(now, domain.getDateCreated());
        assertEquals(now, domain.getDateUpdated());
    }

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity")
    void shouldMapToEntity() {
        var client = Client.create("Test", "test@test.com", "12345678901", "48999999999");

        var entity = ClientJpaMapper.toEntity(client);

        assertEquals(client.getId(), entity.getId());
        assertEquals("Test", entity.getName());
        assertEquals("test@test.com", entity.getEmail());
        assertEquals("12345678901", entity.getDocument());
        assertEquals("48999999999", entity.getPhone());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
    }
}
