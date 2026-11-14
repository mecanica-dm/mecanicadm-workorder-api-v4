package com.mecanicadm.workorder_api.infra.features.workorder.api.openapi;

import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderStatusChangeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Ordem de Serviço", description = "Gerenciamento das ordens de serviço")
public interface WorkOrderHistoryOpenApi {

    @Operation(summary = "Consultar o histórico de status da ordem de serviço",
            description = "Retorna cada mudança de status da OS, da mais antiga para a mais recente, com o status anterior, o novo, "
                    + "o motivo (quando houver), quem mudou e quando.")
    @ApiResponse(responseCode = "200", description = "Histórico de status da ordem de serviço")
    @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content)
    ResponseEntity<List<WorkOrderStatusChangeResponse>> findHistory(UUID id);
}
