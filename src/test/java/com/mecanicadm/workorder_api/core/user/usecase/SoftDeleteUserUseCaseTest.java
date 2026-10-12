package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.usecase.command.SoftDeleteUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftDeleteUserUseCaseTest {

    @Mock
    private UserGateway gateway;

    private SoftDeleteUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SoftDeleteUserUseCase(gateway);
    }

    @Test
    @DisplayName("Deve realizar exclusão lógica (soft delete) com sucesso")
    void shouldDeleteUser() {
        UUID id = UUID.randomUUID();
        SoftDeleteUserCommand command = new SoftDeleteUserCommand(id);
        User user = mock(User.class);

        when(gateway.findById(id)).thenReturn(Optional.of(user));

        useCase.execute(command);

        verify(gateway).findById(id);
        verify(user).softDelete();
        verify(gateway).update(user);
    }

    @Test
    @DisplayName("Deve lançar NotFound quando usuário não existe")
    void shouldThrowNotFoundWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        SoftDeleteUserCommand command = new SoftDeleteUserCommand(id);

        when(gateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserExceptions.NotFound.class, () -> useCase.execute(command));

        verify(gateway).findById(id);
        verify(gateway, never()).update(any());
    }
}
