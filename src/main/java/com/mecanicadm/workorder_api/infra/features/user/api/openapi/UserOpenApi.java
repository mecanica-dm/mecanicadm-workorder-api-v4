package com.mecanicadm.workorder_api.infra.features.user.api.openapi;

import com.mecanicadm.workorder_api.infra.features.user.api.dto.*;
import com.mecanicadm.workorder_api.infra.security.UserAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários e autenticação")
public interface UserOpenApi {

    @Operation(summary = "Autenticar usuário", description = "Realiza o login do usuário e retorna um token JWT")
    @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
    ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginRequest request);

    @Operation(summary = "Solicitar recuperação de senha", description = "Envia um e-mail com link para redefinição de senha")
    @ApiResponse(responseCode = "200", description = "E-mail de recuperação enviado")
    ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request);

    @Operation(summary = "Redefinir senha", description = "Altera a senha do usuário utilizando o token enviado por e-mail")
    @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso")
    ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request);

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    ResponseEntity<UserResponse> findUser(UUID id);

    @Operation(summary = "Criar novo usuário", description = "Cadastra um novo usuário no sistema")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    ResponseEntity<UUID> create(@RequestBody @Valid CreateUserRequest request);

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados do usuário autenticado", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    ResponseEntity<Void> update(@RequestBody @Valid UpdateUserRequest request, @AuthenticationPrincipal UserAdapter userAdapter);

    @Operation(summary = "Excluir usuário", description = "Realiza a exclusão lógica do usuário autenticado", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso")
    ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserAdapter userAdapter);
}
