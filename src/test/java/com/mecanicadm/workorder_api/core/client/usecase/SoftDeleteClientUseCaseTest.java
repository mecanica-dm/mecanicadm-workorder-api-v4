package com.mecanicadm.workorder_api.core.client.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.client.usecase.command.SoftDeleteClientCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftDeleteClientUseCaseTest {

    @Mock
    private ClientGateway repository;

    private SoftDeleteClientUseCase softDeleteClientUseCase;

    private UUID clientId;
    private SoftDeleteClientCommand command;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        softDeleteClientUseCase = new SoftDeleteClientUseCase(repository);
        command = new SoftDeleteClientCommand(clientId);
    }

    @Test
    @DisplayName("Deve realizar a exclusão lógica do cliente com sucesso")
    void shouldSoftDeleteClientSuccessfully() {
        Client client = mock(Client.class);
        when(repository.findById(clientId)).thenReturn(Optional.of(client));

        softDeleteClientUseCase.execute(command);

        verify(repository).findById(clientId);
        verify(client).delete();
        verify(repository).update(client);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir um cliente inexistente")
    void shouldThrowExceptionWhenClientNotFound() {
        when(repository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ClientExceptions.NotFound.class, () -> softDeleteClientUseCase.execute(command));

        verify(repository).findById(clientId);
        verify(repository, never()).update(any(Client.class));
    }
}
