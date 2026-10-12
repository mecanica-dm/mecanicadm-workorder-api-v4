package com.mecanicadm.workorder_api.infra.features.workorder.api.openapi;

import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableBudgetResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.DecideWorkOrderBudgetRequest;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.ManuallyAdjustWorkOrderBudgetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Orçamentos", description = "Endpoints para gerenciamento de orçamentos de ordens de serviço")
public interface WorkOrderBudgetOpenApi {

    @Operation(summary = "Ajustar orçamento manualmente",
            description = "Altera o valor total do orçamento de uma ordem de serviço manualmente")
    @ApiResponse(responseCode = "204", description = "Orçamento ajustado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content)
    ResponseEntity<Void> adjustBudget(UUID workOrderId, ManuallyAdjustWorkOrderBudgetRequest cmd);

    @Operation(summary = "Recalcular orçamento automaticamente",
            description = "Recalcula o valor total do orçamento com base na soma atual de materiais e serviços")
    @ApiResponse(responseCode = "204", description = "Orçamento recalculado com sucesso")
    @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content)
    ResponseEntity<Void> recalculateBudget(UUID workOrderId);

    @Operation(summary = "Enviar orçamento",
            description = "Envia o orçamento da ordem de serviço para o cliente e altera o status para WAITING_DECISION")
    @ApiResponse(responseCode = "204", description = "Orçamento enviado com sucesso")
    @ApiResponse(responseCode = "404", description = "Ordem de serviço ou orçamento não encontrado", content = @Content)
    ResponseEntity<Void> sendBudget(UUID workOrderId);

    @Operation(summary = "Decidir orçamento",
            description = "Aprova ou rejeita o orçamento. Se rejeitado, pode solicitar revisão ou cancelar a OS")
    @ApiResponse(responseCode = "204", description = "Decisão registrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Ordem de serviço ou orçamento não encontrado", content = @Content)
    ResponseEntity<Void> decideBudget(UUID workOrderId, DecideWorkOrderBudgetRequest cmd);

    @Operation(summary = "Imprimir orçamento",
            description = "Retorna um arquivo PDF em Base64 com os dados consolidados do orçamento para impressão")
    @ApiResponse(responseCode = "200", description = "PDF gerado e retornado com sucesso")
    @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content)
    ResponseEntity<PrintableBudgetResponse> printBudget(UUID workOrderId);
}
