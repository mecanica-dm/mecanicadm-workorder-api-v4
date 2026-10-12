package com.mecanicadm.workorder_api.infra.features.workorder.api.openapi;

import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.AddMaterialToWorkOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Materiais da Ordem de Serviço", description = "Endpoints para gerenciamento de materiais em ordens de serviço")
public interface WorkOrderMaterialOpenApi {

    @Operation(summary = "Adicionar material à ordem de serviço", description = "Adiciona um material à ordem de serviço e deduz do estoque")
    @ApiResponse(responseCode = "204", description = "Material adicionado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ordem de serviço ou material não encontrado", content = @Content)
    ResponseEntity<Void> addMaterial(UUID workOrderId, UUID materialId, AddMaterialToWorkOrderRequest request);

    @Operation(summary = "Remover material da ordem de serviço", description = "Remove um material da ordem de serviço e retorna ao estoque")
    @ApiResponse(responseCode = "204", description = "Material removido com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ordem de serviço ou material não encontrado", content = @Content)
    ResponseEntity<Void> removeMaterial(UUID workOrderId, UUID materialId);
}