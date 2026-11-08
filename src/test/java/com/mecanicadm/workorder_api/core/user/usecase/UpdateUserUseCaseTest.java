package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.core.user.usecase.command.UpdateUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    private UserGateway gateway;
    private PasswordEncoder passwordEncoder;
    private UpdateUserUseCase useCase;
    private UUID userId;
    private User existingUser;

    @BeforeEach
    void setUp() {
        gateway = mock(UserGateway.class);
        passwordEncoder = mock(PasswordEncoder.class);

        useCase = new UpdateUserUseCase(gateway, passwordEncoder);

        userId = UUID.randomUUID();

        existingUser = User.create("teste@email.com", "encoded", "Nome Antigo");
        existingUser = User.restore(userId, existingUser.getEmail(), existingUser.getPassword(), existingUser.getName(), existingUser.getRoles(), null, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve atualizar um usuario existente com sucesso (apenas nome)")
    void shouldUpdateExistingUserSuccessfully_NameOnly() {
        UpdateUserCommand command = new UpdateUserCommand(userId, "Novo Nome", null, null);

        when(gateway.findById(userId)).thenReturn(Optional.of(existingUser));

        useCase.execute(command);

        verify(gateway).findById(userId);
        verify(gateway).update(existingUser);
    }

    @Test
    @DisplayName("Deve lancar excecao quando o usuario nao for encontrado para atualizacao")
    void shouldThrowExceptionWhenUserNotFound() {
        UpdateUserCommand command = new UpdateUserCommand(userId, "Novo Nome", null, null);

        when(gateway.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserExceptions.NotFound.class, () -> useCase.execute(command));

        verify(gateway).findById(userId);
        verify(gateway, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar a senha do usuario com sucesso")
    void shouldUpdateUserPasswordSuccessfully() {
        UpdateUserCommand command = new UpdateUserCommand(userId, null, "novaSenha", "123456");

        when(gateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("123456", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaEncoded");

        useCase.execute(command);

        verify(gateway).findById(userId);
        verify(passwordEncoder).matches("123456", "encoded");
        verify(passwordEncoder).encode("novaSenha");
        verify(gateway).update(existingUser);
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar atualizar senha sem informar a senha atual")
    void shouldThrowExceptionWhenUpdatingPasswordWithoutCurrentPassword() {
        UpdateUserCommand command = new UpdateUserCommand(userId, null, "novaSenha", null);

        when(gateway.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(UserExceptions.CurrentPasswordRequired.class, () -> useCase.execute(command));

        verify(gateway).findById(userId);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(gateway, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar atualizar senha com a senha atual incorreta")
    void shouldThrowExceptionWhenUpdatingPasswordWithIncorrectCurrentPassword() {
        UpdateUserCommand command = new UpdateUserCommand(userId, null, "novaSenha", "senhaIncorreta");

        when(gateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("senhaIncorreta", "encoded")).thenReturn(false);

        assertThrows(UserExceptions.BadCredentials.class, () -> useCase.execute(command));

        verify(gateway).findById(userId);
        verify(passwordEncoder).matches("senhaIncorreta", "encoded");
        verify(gateway, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar nome e senha simultaneamente")
    void shouldUpdateNameAndPasswordSimultaneously() {
        UpdateUserCommand command = new UpdateUserCommand(userId, "Novo Nome", "novaSenha", "currentPassword");

        when(gateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPassword", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaEncoded");

        useCase.execute(command);

        verify(gateway).findById(userId);
        verify(passwordEncoder).matches("currentPassword", "encoded");
        verify(passwordEncoder).encode("novaSenha");
        verify(gateway).update(existingUser);
    }

    @Test
    @DisplayName("Deve atualizar usuario com nome vazio (apenas senha)")
    void shouldUpdatePasswordOnlyWhenNameIsBlank() {
        UpdateUserCommand command = new UpdateUserCommand(userId, "", "novaSenha", "currentPassword");

        when(gateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPassword", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaEncoded");

        useCase.execute(command);

        verify(gateway).findById(userId);
        verify(passwordEncoder).matches("currentPassword", "encoded");
        verify(gateway).update(existingUser);
    }
}
