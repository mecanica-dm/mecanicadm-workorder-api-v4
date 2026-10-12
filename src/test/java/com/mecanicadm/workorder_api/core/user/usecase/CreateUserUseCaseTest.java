package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.core.user.usecase.command.CreateUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    private UserGateway gateway;
    private PasswordEncoder passwordEncoder;
    private CreateUserUseCase useCase;
    private CreateUserCommand command;

    @BeforeEach
    void setUp() {
        gateway = mock(UserGateway.class);
        passwordEncoder = mock(PasswordEncoder.class);
        useCase = new CreateUserUseCase(gateway, passwordEncoder);

        command = new CreateUserCommand(
                "teste@email.com",
                "123456",
                "Usuario Teste"
        );
    }

    @Test
    @DisplayName("Deve criar um usuario com sucesso")
    void shouldCreateUserSuccessfully() {
        when(gateway.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("encodedPassword");

        User savedUser = mock(User.class);
        UUID expectedId = UUID.randomUUID();
        when(savedUser.getId()).thenReturn(expectedId);
        when(gateway.create(any(User.class))).thenReturn(savedUser);

        UUID resultId = useCase.execute(command);

        assertNotNull(resultId);
        assertEquals(expectedId, resultId);
        verify(gateway).existsByEmail(command.email());
        verify(passwordEncoder).encode(command.password());
        verify(gateway).create(any(User.class));
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario ja existir")
    void shouldThrowExceptionWhenUserAlreadyExists() {
        when(gateway.existsByEmail(command.email())).thenReturn(true);

        assertThrows(UserExceptions.UserAlreadyExists.class, () -> useCase.execute(command));

        verify(gateway).existsByEmail(command.email());
        verify(passwordEncoder, never()).encode(anyString());
        verify(gateway, never()).create(any(User.class));
    }
}
