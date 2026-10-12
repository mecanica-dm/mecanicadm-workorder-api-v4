package com.mecanicadm.workorder_api.infra.features.workorder.api.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Decisão de Orçamento (Público)", description = "Endpoints públicos para resposta do cliente ao orçamento via link/token")
public interface BudgetDecisionPublicOpenApi {

    @Operation(summary = "Exibir formulário de decisão do orçamento",
            description = "Renderiza um formulário HTML para que o cliente aprove, rejeite ou solicite alterações no orçamento")
    @ApiResponse(responseCode = "200", description = "Formulário HTML retornado com sucesso")
    @ApiResponse(responseCode = "404", description = "Token não encontrado", content = @Content)
    ResponseEntity<String> form(
            @Parameter(description = "Token de identificação do orçamento") String token,
            @Parameter(description = "Ação desejada: APPROVED, REJECTED ou CHANGES_REQUESTED") String action);

    @Operation(summary = "Processar decisão do orçamento",
            description = "Registra a decisão do cliente (aprovação, rejeição ou solicitação de alterações) sobre o orçamento")
    @ApiResponse(responseCode = "200", description = "Decisão processada com sucesso, página de confirmação retornada")
    @ApiResponse(responseCode = "404", description = "Token não encontrado", content = @Content)
    ResponseEntity<String> decide(
            @Parameter(description = "Token de identificação do orçamento") String token,
            @Parameter(description = "Ação desejada: APPROVED, REJECTED ou CHANGES_REQUESTED") String action,
            @Parameter(description = "Observação do cliente (opcional para aprovação, obrigatória para rejeição/alterações)") String observation);
}
