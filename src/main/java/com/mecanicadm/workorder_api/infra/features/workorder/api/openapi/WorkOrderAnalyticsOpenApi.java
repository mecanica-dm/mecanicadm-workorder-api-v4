package com.mecanicadm.workorder_api.infra.features.workorder.api.openapi;

import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborExecutionReportResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderExecutionReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@Tag(name = "Analytics", description = "Endpoints para relatórios e análises de desempenho")
public interface WorkOrderAnalyticsOpenApi {

    @Operation(summary = "Relatório de tempo de execução de Ordens de Serviço", description = "Gera estatísticas de tempo de execução das OSs concluídas no período")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    ResponseEntity<WorkOrderExecutionReportResponse> getWorkOrderExecutionReport(
            @Parameter(description = "Data inicial (inclusive)") LocalDate initialDate,
            @Parameter(description = "Data final (inclusive)") LocalDate finalDate);

    @Operation(summary = "Relatório de tempo de execução de Mão de Obra", description = "Gera estatísticas de tempo de execução agrupadas por tipo de serviço")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    ResponseEntity<LaborExecutionReportResponse> getLaborExecutionReport(
            @Parameter(description = "Data inicial (inclusive)") LocalDate initialDate,
            @Parameter(description = "Data final (inclusive)") LocalDate finalDate);
}
