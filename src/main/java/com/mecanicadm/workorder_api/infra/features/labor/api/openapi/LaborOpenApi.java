package com.mecanicadm.workorder_api.infra.features.labor.api.openapi;

import com.mecanicadm.workorder_api.infra.features.labor.api.dto.request.CreateLaborRequest;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.request.UpdateLaborRequest;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Mao de obra", description = "Endpoints para gerenciamento de mao de obra")
public interface LaborOpenApi {

    @Operation(summary = "Criar novo servico")
    @ApiResponse(responseCode = "201", description = "Servico cadastrado com sucesso")
    ResponseEntity<UUID> create(CreateLaborRequest request);

    @Operation(summary = "Atualizar servico")
    @ApiResponse(responseCode = "204", description = "Servico atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Servico nao encontrado")
    ResponseEntity<Void> update(@Parameter(description = "Identificador do servico") UUID id,
                                UpdateLaborRequest request);

    @Operation(summary = "Excluir servico")
    @ApiResponse(responseCode = "204", description = "Servico excluido com sucesso")
    @ApiResponse(responseCode = "404", description = "Servico nao encontrado")
    ResponseEntity<Void> delete(@Parameter(description = "Identificador do servico") UUID id);

    @Operation(summary = "Buscar servico por id")
    @ApiResponse(responseCode = "200", description = "Servico encontrado")
    @ApiResponse(responseCode = "404", description = "Servico nao encontrado")
    ResponseEntity<LaborResponse> findById(@Parameter(description = "Identificador do servico") UUID id);

    @Operation(summary = "Listar servicos")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    ResponseEntity<Page<LaborResponse>> getAll(
            @Parameter(description = "Filtro parcial por nome") String name,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable);
}
