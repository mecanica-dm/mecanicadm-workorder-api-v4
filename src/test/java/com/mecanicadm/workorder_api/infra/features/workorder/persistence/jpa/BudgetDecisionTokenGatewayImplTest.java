package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.BudgetDecisionToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetDecisionTokenGatewayImplTest {

    @Mock
    private BudgetDecisionTokenJpaRepository jpaRepository;

    @InjectMocks
    private BudgetDecisionTokenGatewayImpl gateway;

    @Test
    @DisplayName("Deve criar token com sucesso")
    void shouldCreateToken() {
        UUID workOrderId = UUID.randomUUID();
        BudgetDecisionToken token = BudgetDecisionToken.create(workOrderId);

        when(jpaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BudgetDecisionToken result = gateway.create(token);

        assertNotNull(result);
        verify(jpaRepository).save(any());
    }

    @Test
    @DisplayName("Deve buscar token por valor")
    void shouldFindByToken() {
        UUID workOrderId = UUID.randomUUID();
        String tokenValue = "test-token";
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), workOrderId, tokenValue, false, LocalDateTime.now());

        when(jpaRepository.findByToken(tokenValue)).thenReturn(
                Optional.of(new com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.BudgetDecisionTokenJpaEntity(
                        token.getId(), workOrderId, tokenValue, false, token.getCreatedAt())));

        Optional<BudgetDecisionToken> result = gateway.findByToken(tokenValue);

        assertTrue(result.isPresent());
        assertEquals(tokenValue, result.get().getToken());
    }

    @Test
    @DisplayName("Deve retornar vazio quando token nao for encontrado")
    void shouldReturnEmptyWhenTokenNotFound() {
        when(jpaRepository.findByToken("nonexistent")).thenReturn(Optional.empty());

        Optional<BudgetDecisionToken> result = gateway.findByToken("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar token com sucesso")
    void shouldUpdateToken() {
        UUID workOrderId = UUID.randomUUID();
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), workOrderId, "token-123", true, LocalDateTime.now());

        when(jpaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        gateway.update(token);

        verify(jpaRepository).save(any());
    }
}
