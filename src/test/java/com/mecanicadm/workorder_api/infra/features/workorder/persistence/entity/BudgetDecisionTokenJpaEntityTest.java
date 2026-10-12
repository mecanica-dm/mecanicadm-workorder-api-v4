package com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BudgetDecisionTokenJpaEntityTest {

    @Test
    @DisplayName("Deve criar entidade com todos os campos")
    void shouldCreateEntityWithAllFields() {
        UUID id = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        String token = "test-token-123";
        LocalDateTime createdAt = LocalDateTime.now();

        var entity = new BudgetDecisionTokenJpaEntity(id, workOrderId, token, false, createdAt);

        assertEquals(id, entity.getId());
        assertEquals(workOrderId, entity.getWorkOrderId());
        assertEquals(token, entity.getToken());
        assertFalse(entity.isUsed());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar entidade como usada")
    void shouldCreateEntityAsUsed() {
        UUID id = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        var entity = new BudgetDecisionTokenJpaEntity(id, workOrderId, "token-used", true, createdAt);

        assertTrue(entity.isUsed());
    }

    @Test
    @DisplayName("Deve alterar status used via setter")
    void shouldSetUsedViaSetter() {
        var entity = new BudgetDecisionTokenJpaEntity(
                UUID.randomUUID(), UUID.randomUUID(), "token", false, LocalDateTime.now());

        entity.setUsed(true);

        assertTrue(entity.isUsed());
    }

    @Test
    @DisplayName("Deve criar entidade via construtor protegido (JPA)")
    void shouldCreateEntityViaProtectedConstructor() {
        var entity = new BudgetDecisionTokenJpaEntity();

        assertNull(entity.getId());
        assertNull(entity.getWorkOrderId());
        assertNull(entity.getToken());
        assertEquals(false, entity.isUsed());
        assertNull(entity.getCreatedAt());
    }
}
