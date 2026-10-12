package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.BudgetDecisionToken;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.BudgetDecisionTokenJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BudgetDecisionTokenJpaMapperTest {

    @Test
    @DisplayName("Deve mapear JpaEntity para dominio")
    void shouldMapToDomain() {
        UUID id = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        String tokenValue = "test-token";
        LocalDateTime createdAt = LocalDateTime.now();
        var entity = new BudgetDecisionTokenJpaEntity(id, workOrderId, tokenValue, false, createdAt);

        var domain = BudgetDecisionTokenJpaMapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(id, domain.getId());
        assertEquals(workOrderId, domain.getWorkOrderId());
        assertEquals(tokenValue, domain.getToken());
        assertFalse(domain.isUsed());
        assertEquals(createdAt, domain.getCreatedAt());
    }

    @Test
    @DisplayName("Deve mapear dominio para JpaEntity")
    void shouldMapToEntity() {
        UUID id = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        String tokenValue = "test-token";
        LocalDateTime createdAt = LocalDateTime.now();
        var domain = BudgetDecisionToken.restore(id, workOrderId, tokenValue, true, createdAt);

        var entity = BudgetDecisionTokenJpaMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(workOrderId, entity.getWorkOrderId());
        assertEquals(tokenValue, entity.getToken());
        assertTrue(entity.isUsed());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    @DisplayName("Deve retornar null quando entidade for nula")
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(BudgetDecisionTokenJpaMapper.toDomain(null));
    }

    @Test
    @DisplayName("Deve retornar null quando dominio for nulo")
    void shouldReturnNullWhenDomainIsNull() {
        assertNull(BudgetDecisionTokenJpaMapper.toEntity(null));
    }
}
