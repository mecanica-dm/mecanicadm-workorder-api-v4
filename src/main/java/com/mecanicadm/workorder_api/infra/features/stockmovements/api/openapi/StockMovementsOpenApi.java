package com.mecanicadm.workorder_api.infra.features.stockmovements.api.openapi;

import com.mecanicadm.workorder_api.infra.features.stockmovements.api.dto.response.StockStatementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "Stock Movements", description = "Endpoints para gerenciamento de movimentações de estoque")
public interface StockMovementsOpenApi {

    @Operation(summary = "Busca o extrato de movimentações de um material",
            description = "Retorna o saldo atual e o histórico de todas as movimentações de estoque para o material especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extrato retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Material não encontrado")
    })
    ResponseEntity<StockStatementResponse> getStatement(
            @Parameter(description = "ID do material", required = true)
            @PathVariable UUID materialId);
}
