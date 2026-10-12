package com.mecanicadm.workorder_api.infra.features.workorder.api.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Fluxo de trabalho da Ordem de Servico", description = "Endpoints para fluxo de trabalho da ordem de serviço")
public interface WorkOrderWorkflowOpenApi {

    @Operation(summary = "Diagnosticar ordem de servico", description = "Diagnostica a ordem de serviço e gera orçamento")
    @ApiResponse(responseCode = "200", description = "Ordem diagnosticada com sucesso",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    @ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ordem de servico nao encontrada", content = @Content)
    ResponseEntity<UUID> diagnose(UUID id);

    @Operation(summary = "Iniciar execucao da ordem de servico", description = "Envia ordem de serviço para em execução")
    @ApiResponse(responseCode = "204", description = "Execucao iniciada com sucesso")
    @ApiResponse(responseCode = "404", description = "Ordem de servico nao encontrada", content = @Content)
    ResponseEntity<Void> startExecution(UUID id);

    @Operation(summary = "Finalizar execucao da ordem de servico", description = "Finaliza a execucao da ordem de serviço")
    @ApiResponse(responseCode = "204", description = "Execucao finalizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Ordem de servico nao encontrada", content = @Content)
    ResponseEntity<Void> finishExecution(UUID id);

    @Operation(summary = "Registrar retirada do veiculo", description = "Registra entrega do veículo ao cliente da ordem de serviço")
    @ApiResponse(responseCode = "204", description = "Retirada registrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Status invalido para retirada", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ordem de servico nao encontrada", content = @Content)
    ResponseEntity<Void> deliver(UUID id);
}

