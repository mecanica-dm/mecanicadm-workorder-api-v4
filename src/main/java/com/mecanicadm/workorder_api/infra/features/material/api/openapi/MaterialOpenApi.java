package com.mecanicadm.workorder_api.infra.features.material.api.openapi;

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.infra.features.material.api.dto.request.CreateMaterialRequest;
import com.mecanicadm.workorder_api.infra.features.material.api.dto.request.UpdateMaterialRequest;
import com.mecanicadm.workorder_api.infra.features.material.api.dto.response.MaterialResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Materiais", description = "Endpoints para gerenciamento de materiais")
public interface MaterialOpenApi {

    @Operation(summary = "Criar novo material", description = "Cadastra um novo material no sistema e inicializa o estoque")
    @ApiResponse(responseCode = "201", description = "Material criado com sucesso",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    ResponseEntity<UUID> create(CreateMaterialRequest request);

    @Operation(summary = "Atualizar material", description = "Atualiza os dados de um material existente")
    @ApiResponse(responseCode = "200", description = "Material atualizado com sucesso", content = @Content)
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Material não encontrado", content = @Content)
    ResponseEntity<Void> update(UUID id, UpdateMaterialRequest request);

    @Operation(summary = "Buscar material por ID", description = "Retorna os detalhes de um material específico")
    @ApiResponse(responseCode = "200", description = "Material encontrado",
            content = @Content(schema = @Schema(implementation = MaterialResponse.class)))
    @ApiResponse(responseCode = "404", description = "Material não encontrado", content = @Content)
    ResponseEntity<MaterialResponse> findById(UUID id);

    @Operation(summary = "Listar com Filtros", description = "Lista materiais com paginação e filtros opcionais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<Page<MaterialResponse>> findAll(
            @Parameter(description = "Nome do material") String name,
            @Parameter(description = "Marca do material") String brand,
            @Parameter(description = "Tipo do material") MaterialType type,
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "Excluir material", description = "Realiza a exclusão lógica de um material")
    @ApiResponse(responseCode = "204", description = "Material excluído com sucesso", content = @Content)
    @ApiResponse(responseCode = "404", description = "Material não encontrado", content = @Content)
    ResponseEntity<Void> delete(UUID id);
}
