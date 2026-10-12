package com.mecanicadm.workorder_api.core.workorder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BudgetDecisionTokenTest {

    @Test
    @DisplayName("Deve criar token com dados válidos")
    void shouldCreateTokenWithValidData() {
        UUID workOrderId = UUID.randomUUID();
        BudgetDecisionToken token = BudgetDecisionToken.create(workOrderId);

        assertNotNull(token.getId());
        assertEquals(workOrderId, token.getWorkOrderId());
        assertNotNull(token.getToken());
        assertFalse(token.isUsed());
        assertNotNull(token.getCreatedAt());
    }

    @Test
    @DisplayName("Deve restaurar token a partir de dados existentes")
    void shouldRestoreToken() {
        UUID id = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        String tokenValue = "restored-token";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);

        BudgetDecisionToken token = BudgetDecisionToken.restore(id, workOrderId, tokenValue, false, createdAt);

        assertEquals(id, token.getId());
        assertEquals(workOrderId, token.getWorkOrderId());
        assertEquals(tokenValue, token.getToken());
        assertFalse(token.isUsed());
        assertEquals(createdAt, token.getCreatedAt());
    }

    @Test
    @DisplayName("Deve marcar token como utilizado")
    void shouldMarkTokenAsUsed() {
        BudgetDecisionToken token = BudgetDecisionToken.create(UUID.randomUUID());

        assertFalse(token.isUsed());
        token.markAsUsed();
        assertTrue(token.isUsed());
    }

    @Test
    @DisplayName("Deve considerar token válido quando não utilizado e não expirado")
    void shouldConsiderTokenValid() {
        BudgetDecisionToken token = BudgetDecisionToken.create(UUID.randomUUID());
        assertTrue(token.isValid());
    }

    @Test
    @DisplayName("Deve considerar token inválido quando utilizado")
    void shouldConsiderTokenInvalidWhenUsed() {
        BudgetDecisionToken token = BudgetDecisionToken.create(UUID.randomUUID());
        token.markAsUsed();
        assertFalse(token.isValid());
    }

    @Test
    @DisplayName("Deve considerar token expirado após 24 horas")
    void shouldConsiderTokenExpired() {
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), UUID.randomUUID(), "token", false,
                LocalDateTime.now().minusHours(25));
        assertTrue(token.isExpired());
        assertFalse(token.isValid());
    }

    @Test
    @DisplayName("Deve considerar token não expirado quando criado há menos de 24 horas")
    void shouldConsiderTokenNotExpired() {
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), UUID.randomUUID(), "token", false,
                LocalDateTime.now().minusHours(1));
        assertFalse(token.isExpired());
        assertTrue(token.isValid());
    }
}
