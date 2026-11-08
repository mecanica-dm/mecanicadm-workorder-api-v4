package com.mecanicadm.workorder_api.infra.features.client.api.openapi;

import com.mecanicadm.workorder_api.infra.features.client.api.dto.request.CreateClientRequest;
import com.mecanicadm.workorder_api.infra.features.client.api.dto.request.UpdateClientRequest;
import com.mecanicadm.workorder_api.infra.features.client.api.dto.response.ClientResponse;
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

@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
public interface ClientOpenApi {

    @Operation(summary = "Criar novo cliente", description = "Cadastra um novo cliente no sistema e cria seu registro de usuário")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "409", description = "Email ou Documento já cadastrado", content = @Content)
    ResponseEntity<UUID> create(CreateClientRequest request);

    @Operation(summary = "Listar com Filtros", description = "Lista clientes com paginação (Page) e filtros opcionais de nome e documento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<Page<ClientResponse>> getAll(
            @Parameter(description = "Nome do cliente") String name,
            @Parameter(description = "Documento (CPF/CNPJ) do cliente") String document,
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso", content = @Content)
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Novo Documento já está em uso", content = @Content)
    ResponseEntity<Void> update(UUID id, UpdateClientRequest request);

    @Operation(summary = "Excluir cliente", description = "Realiza a exclusão lógica de um cliente e seu usuário associado")
    @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso", content = @Content)
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    ResponseEntity<Void> delete(UUID id);
}
