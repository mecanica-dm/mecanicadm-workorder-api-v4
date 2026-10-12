package com.mecanicadm.workorder_api.core.client.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.client.usecase.command.UpdateClientCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateClientUseCaseTest {

    @Mock
    private ClientGateway repository;

    private UpdateClientUseCase updateClientUseCase;

    private UpdateClientCommand command;
    private UUID clientId;
    private Client existingClient;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        updateClientUseCase = new UpdateClientUseCase(repository);
        command = new UpdateClientCommand(
                clientId,
                "Updated Client",
                "updated@example.com",
                "43615632009",
                "48988888888"
        );
        existingClient = mock(Client.class);
    }

    @Test
    @DisplayName("Deve atualizar o cliente com sucesso")
    void shouldUpdateClientSuccessfully() {
        when(repository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(existingClient.getId()).thenReturn(clientId);
        when(repository.existsByDocumentAndIdNot(command.document(), clientId)).thenReturn(false);
        when(repository.existsByEmailAndIdNot(command.email(), clientId)).thenReturn(false);

        updateClientUseCase.execute(command);

        verify(repository).findById(clientId);
        verify(existingClient).update(command.name(), command.email(), command.document(), command.phone());
        verify(repository).update(existingClient);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um cliente inexistente")
    void shouldThrowExceptionWhenClientNotFound() {
        when(repository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ClientExceptions.NotFound.class, () -> updateClientUseCase.execute(command));

        verify(repository).findById(clientId);
        verify(repository, never()).update(any(Client.class));
    }

    @Test
    @DisplayName("Deve permitir atualização quando Documento e E-mail forem iguais aos atuais")
    void shouldUpdateSuccessfullyWhenDocumentAndEmailAreSame() {
        when(repository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(existingClient.getId()).thenReturn(clientId);

        assertDoesNotThrow(() -> updateClientUseCase.execute(command));

        verify(repository).existsByEmailAndIdNot(command.email(), clientId);
        verify(repository).existsByDocumentAndIdNot(command.document(), clientId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o novo e-mail já está em uso por outro cliente")
    void shouldThrowExceptionWhenEmailExistsForOtherClient() {
        when(repository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(existingClient.getId()).thenReturn(clientId);
        when(repository.existsByEmailAndIdNot(command.email(), clientId)).thenReturn(true);

        assertThrows(ClientExceptions.EmailExists.class, () -> updateClientUseCase.execute(command));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o novo Documento já está em uso por outro cliente")
    void shouldThrowExceptionWhenDocumentExistsForOtherClient() {
        when(repository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(existingClient.getId()).thenReturn(clientId);
        when(repository.existsByDocumentAndIdNot(command.document(), clientId)).thenReturn(true);

        assertThrows(ClientExceptions.DocumentExists.class, () -> updateClientUseCase.execute(command));
    }
}
