package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.ProcessBudgetDecisionByTokenUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetDecisionPublicControllerTest {

    @Mock
    private ProcessBudgetDecisionByTokenUseCase processBudgetDecisionByTokenUseCase;

    private BudgetDecisionPublicController controller;

    @BeforeEach
    void setUp() {
        controller = new BudgetDecisionPublicController(processBudgetDecisionByTokenUseCase);
    }

    @Test
    @DisplayName("Deve retornar formulario de aprovacao com token valido")
    void shouldReturnFormPageForApproval() {
        var response = controller.form("valid-token", "APPROVED");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.TEXT_HTML, response.getHeaders().getContentType());
        assertTrue(response.getBody().contains("Aprovar Orçamento"));
        assertTrue(response.getBody().contains("valid-token"));
        verify(processBudgetDecisionByTokenUseCase).validateToken("valid-token");
    }

    @Test
    @DisplayName("Deve retornar formulario de rejeicao com required")
    void shouldReturnFormPageForRejection() {
        var response = controller.form("valid-token", "REJECTED");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Rejeitar Orçamento"));
        assertTrue(response.getBody().contains("required"));
    }

    @Test
    @DisplayName("Deve retornar formulario de solicitacao de alteracoes")
    void shouldReturnFormPageForChangesRequested() {
        var response = controller.form("valid-token", "CHANGES_REQUESTED");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Solicitar Alterações"));
        assertTrue(response.getBody().contains("required"));
    }

    @Test
    @DisplayName("Deve retornar formulario para acao desconhecida")
    void shouldReturnFormPageForUnknownAction() {
        var response = controller.form("valid-token", "OTHER");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Resposta ao Orçamento"));
    }

    @Test
    @DisplayName("Deve retornar pagina de erro quando token ja foi utilizado")
    void shouldReturnErrorPageWhenTokenAlreadyUsed() {
        doThrow(WorkOrderExceptions.BudgetTokenInvalid.of("já utilizado")).when(processBudgetDecisionByTokenUseCase).validateToken("used-token");

        var response = controller.form("used-token", "APPROVED");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Link já utilizado"));
    }

    @Test
    @DisplayName("Deve retornar pagina de erro quando token nao for encontrado")
    void shouldReturnErrorPageWhenTokenNotFound() {
        doThrow(new WorkOrderExceptions.BudgetTokenNotFound()).when(processBudgetDecisionByTokenUseCase).validateToken("invalid-token");

        var response = controller.form("invalid-token", "APPROVED");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Link inválido"));
    }

    @Test
    @DisplayName("Deve processar decisao com sucesso")
    void shouldProcessDecisionSuccessfully() {
        var response = controller.decide("valid-token", "APPROVED", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Orçamento aprovado com sucesso!"));
        verify(processBudgetDecisionByTokenUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve processar rejeicao com observacao")
    void shouldProcessRejectionWithObservation() {
        var response = controller.decide("valid-token", "REJECTED", "Valor muito alto");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Orçamento rejeitado"));
        verify(processBudgetDecisionByTokenUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve retornar pagina de erro ao decidir com token invalido")
    void shouldReturnErrorPageWhenDecideWithInvalidToken() {
        doThrow(WorkOrderExceptions.BudgetTokenInvalid.of("já utilizado")).when(processBudgetDecisionByTokenUseCase).execute(any());

        var response = controller.decide("used-token", "APPROVED", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Link já utilizado"));
    }

    @Test
    @DisplayName("Deve retornar pagina de erro ao decidir com token inexistente")
    void shouldReturnErrorPageWhenDecideWithNotFoundToken() {
        doThrow(new WorkOrderExceptions.BudgetTokenNotFound()).when(processBudgetDecisionByTokenUseCase).execute(any());

        var response = controller.decide("nonexistent-token", "APPROVED", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Link inválido"));
    }
}
