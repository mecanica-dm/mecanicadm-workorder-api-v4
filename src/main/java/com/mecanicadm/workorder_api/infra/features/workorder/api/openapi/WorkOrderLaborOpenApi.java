package com.mecanicadm.workorder_api.infra.features.workorder.api.openapi;

import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderLaborItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Mão de Obra de Ordem de Serviço", description = "Endpoints para gerenciamento de execução de mão de obra em ordens de serviço")
public interface WorkOrderLaborOpenApi {

    @Operation(summary = "Adicionar serviço à ordem de serviço", description = "Adiciona um serviço de mão de obra à ordem de serviço")
    @ApiResponse(responseCode = "204", description = "Serviço adicionado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ordem de serviço ou serviço de mão de obra não encontrado", content = @Content)
    ResponseEntity<Void> addLabor(UUID workOrderId, UUID laborItemId);

    @Operation(summary = "Remover serviço da ordem de serviço", description = "Remove um serviço de mão de obra da ordem de serviço")
    @ApiResponse(responseCode = "204", description = "Serviço removido com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ordem de serviço ou serviço de mão de obra não encontrado", content = @Content)
    ResponseEntity<Void> removeLabor(UUID workOrderId, UUID laborItemId);

    @Operation(summary = "Iniciar execução de mão de obra", description = "Inicia o cronômetro para um item de mão de obra específico")
    @ApiResponse(responseCode = "204", description = "Execução iniciada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou Ordem de Serviço não está em execução", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ordem de serviço ou item de mão de obra não encontrado", content = @Content)
    ResponseEntity<Void> startLabor(UUID workOrderId, UUID laborItemId);

    @Operation(summary = "Finalizar execução de mão de obra", description = "Finaliza o cronômetro para um item de mão de obra específico")
    @ApiResponse(responseCode = "204", description = "Execução finalizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Ordem de serviço ou item de mão de obra não encontrado", content = @Content)
    ResponseEntity<Void> finishLabor(UUID workOrderId, UUID laborItemId);

    @Operation(summary = "Buscar item de mão de obra por ID", description = "Retorna os detalhes de um item de mão de obra específico de uma ordem de serviço")
    @ApiResponse(responseCode = "200", description = "Item de mão de obra encontrado", content = @Content(schema = @Schema(implementation = WorkOrderLaborItemResponse.class)))
    @ApiResponse(responseCode = "404", description = "Ordem de serviço ou item de mão de obra não encontrado", content = @Content)
    ResponseEntity<WorkOrderLaborItemResponse> findById(UUID workOrderId, UUID laborItemId);
}
