package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.BudgetDecisionToken;
import com.mecanicadm.workorder_api.core.workorder.domain.port.BudgetDecisionTokenGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DecideWorkOrderBudgetCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.ProcessBudgetDecisionByTokenCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessBudgetDecisionByTokenUseCaseTest {

    @Mock
    private BudgetDecisionTokenGateway tokenGateway;

    @Mock
    private DecideWorkOrderBudgetUseCase decideWorkOrderBudgetUseCase;

    @InjectMocks
    private ProcessBudgetDecisionByTokenUseCase useCase;

    @Test
    @DisplayName("Deve processar aprovação via token com sucesso")
    void shouldProcessApprovalViaToken() {
        UUID workOrderId = UUID.randomUUID();
        String tokenValue = "test-token-123";
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), workOrderId, tokenValue, false, LocalDateTime.now());

        when(tokenGateway.findByToken(tokenValue)).thenReturn(Optional.of(token));

        useCase.execute(new ProcessBudgetDecisionByTokenCommand(tokenValue, "APPROVED", null));

        ArgumentCaptor<DecideWorkOrderBudgetCommand> captor = ArgumentCaptor.forClass(DecideWorkOrderBudgetCommand.class);
        verify(decideWorkOrderBudgetUseCase).execute(captor.capture());
        assertEquals(workOrderId, captor.getValue().workOrderId());
        assertEquals("APPROVED", captor.getValue().decision().name());
        assertNull(captor.getValue().observation());
        verify(tokenGateway).update(argThat(t -> t.isUsed()));
    }

    @Test
    @DisplayName("Deve processar rejeição via token com motivo informado")
    void shouldProcessRejectionViaTokenWithReason() {
        UUID workOrderId = UUID.randomUUID();
        String tokenValue = "test-token-456";
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), workOrderId, tokenValue, false, LocalDateTime.now());

        when(tokenGateway.findByToken(tokenValue)).thenReturn(Optional.of(token));

        String reason = "O valor está muito alto, preciso de uma cotação menor";
        useCase.execute(new ProcessBudgetDecisionByTokenCommand(tokenValue, "REJECTED", reason));

        ArgumentCaptor<DecideWorkOrderBudgetCommand> captor = ArgumentCaptor.forClass(DecideWorkOrderBudgetCommand.class);
        verify(decideWorkOrderBudgetUseCase).execute(captor.capture());
        assertEquals("REJECTED", captor.getValue().decision().name());
        assertEquals(reason, captor.getValue().observation());
        verify(tokenGateway).update(argThat(t -> t.isUsed()));
    }

    @Test
    @DisplayName("Deve processar rejeição via token sem motivo (fallback)")
    void shouldProcessRejectionViaTokenWithoutReason() {
        UUID workOrderId = UUID.randomUUID();
        String tokenValue = "test-token-456b";
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), workOrderId, tokenValue, false, LocalDateTime.now());

        when(tokenGateway.findByToken(tokenValue)).thenReturn(Optional.of(token));

        useCase.execute(new ProcessBudgetDecisionByTokenCommand(tokenValue, "REJECTED", null));

        ArgumentCaptor<DecideWorkOrderBudgetCommand> captor = ArgumentCaptor.forClass(DecideWorkOrderBudgetCommand.class);
        verify(decideWorkOrderBudgetUseCase).execute(captor.capture());
        assertEquals("REJECTED", captor.getValue().decision().name());
        assertNull(captor.getValue().observation());
        verify(tokenGateway).update(argThat(t -> t.isUsed()));
    }

    @Test
    @DisplayName("Deve processar solicitação de alterações via token com descrição")
    void shouldProcessChangesRequestedViaTokenWithReason() {
        UUID workOrderId = UUID.randomUUID();
        String tokenValue = "test-token-789";
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), workOrderId, tokenValue, false, LocalDateTime.now());

        when(tokenGateway.findByToken(tokenValue)).thenReturn(Optional.of(token));

        String reason = "Preciso que inclua a troca do filtro de ar condicionado";
        useCase.execute(new ProcessBudgetDecisionByTokenCommand(tokenValue, "CHANGES_REQUESTED", reason));

        ArgumentCaptor<DecideWorkOrderBudgetCommand> captor = ArgumentCaptor.forClass(DecideWorkOrderBudgetCommand.class);
        verify(decideWorkOrderBudgetUseCase).execute(captor.capture());
        assertEquals("CHANGES_REQUESTED", captor.getValue().decision().name());
        assertEquals(reason, captor.getValue().observation());
        verify(tokenGateway).update(argThat(t -> t.isUsed()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando token não for encontrado")
    void shouldThrowExceptionWhenTokenNotFound() {
        when(tokenGateway.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.BudgetTokenNotFound.class,
                () -> useCase.execute(new ProcessBudgetDecisionByTokenCommand("invalid-token", "APPROVED", null)));

        verify(decideWorkOrderBudgetUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando token já foi utilizado")
    void shouldThrowExceptionWhenTokenAlreadyUsed() {
        UUID workOrderId = UUID.randomUUID();
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), workOrderId, "used-token", true, LocalDateTime.now().minusHours(1));

        when(tokenGateway.findByToken("used-token")).thenReturn(Optional.of(token));

        WorkOrderExceptions.BudgetTokenInvalid ex = assertThrows(WorkOrderExceptions.BudgetTokenInvalid.class,
                () -> useCase.execute(new ProcessBudgetDecisionByTokenCommand("used-token", "APPROVED", null)));
        assertEquals("work.order.budget.token.invalid", ex.getMessageKey());
        assertEquals("já utilizado", ex.getArgs()[0]);
        verify(decideWorkOrderBudgetUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando token estiver expirado")
    void shouldThrowExceptionWhenTokenExpired() {
        UUID workOrderId = UUID.randomUUID();
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), workOrderId, "expired-token", false, LocalDateTime.now().minusHours(25));

        when(tokenGateway.findByToken("expired-token")).thenReturn(Optional.of(token));

        WorkOrderExceptions.BudgetTokenInvalid ex = assertThrows(WorkOrderExceptions.BudgetTokenInvalid.class,
                () -> useCase.execute(new ProcessBudgetDecisionByTokenCommand("expired-token", "APPROVED", null)));
        assertEquals("work.order.budget.token.invalid", ex.getMessageKey());
        assertEquals("expirado", ex.getArgs()[0]);
        verify(decideWorkOrderBudgetUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve validar token valido com sucesso")
    void shouldValidateValidTokenSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), workOrderId, "valid-token", false, LocalDateTime.now());

        when(tokenGateway.findByToken("valid-token")).thenReturn(Optional.of(token));

        assertDoesNotThrow(() -> useCase.validateToken("valid-token"));
        verify(tokenGateway).findByToken("valid-token");
    }

    @Test
    @DisplayName("Deve lancar excecao ao validar token nao encontrado")
    void shouldThrowExceptionWhenValidateTokenNotFound() {
        when(tokenGateway.findByToken("missing-token")).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.BudgetTokenNotFound.class,
                () -> useCase.validateToken("missing-token"));
    }

    @Test
    @DisplayName("Deve lancar excecao ao validar token ja utilizado")
    void shouldThrowExceptionWhenValidateTokenAlreadyUsed() {
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), UUID.randomUUID(), "used-token", true, LocalDateTime.now().minusHours(1));

        when(tokenGateway.findByToken("used-token")).thenReturn(Optional.of(token));

        WorkOrderExceptions.BudgetTokenInvalid ex = assertThrows(WorkOrderExceptions.BudgetTokenInvalid.class,
                () -> useCase.validateToken("used-token"));
        assertEquals("já utilizado", ex.getArgs()[0]);
    }

    @Test
    @DisplayName("Deve lancar excecao ao validar token expirado")
    void shouldThrowExceptionWhenValidateTokenExpired() {
        BudgetDecisionToken token = BudgetDecisionToken.restore(
                UUID.randomUUID(), UUID.randomUUID(), "expired-token", false, LocalDateTime.now().minusHours(25));

        when(tokenGateway.findByToken("expired-token")).thenReturn(Optional.of(token));

        WorkOrderExceptions.BudgetTokenInvalid ex = assertThrows(WorkOrderExceptions.BudgetTokenInvalid.class,
                () -> useCase.validateToken("expired-token"));
        assertEquals("expirado", ex.getArgs()[0]);
    }
}
