package com.mecanicadm.workorder_api.core.client.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.client.usecase.command.CreateClientCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateClientUseCaseTest {

    @Mock
    private ClientGateway repository;

    private CreateClientUseCase createClientUseCase;

    private CreateClientCommand command;

    @BeforeEach
    void setUp() {
        createClientUseCase = new CreateClientUseCase(repository);
        command = new CreateClientCommand(
                "Test Client",
                "test@example.com",
                "17871234053",
                "48999999999"
        );
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso quando e-mail e Documento não existem")
    void shouldCreateClientSuccessfully() {
        when(repository.existsByEmail(command.email())).thenReturn(false);
        when(repository.existsByDocument(command.document())).thenReturn(false);

        Client savedClient = mock(Client.class);
        UUID expectedId = UUID.randomUUID();
        when(savedClient.getId()).thenReturn(expectedId);
        when(repository.create(any(Client.class))).thenReturn(savedClient);

        UUID resultId = createClientUseCase.execute(command);

        assertNotNull(resultId);
        assertEquals(expectedId, resultId);
        verify(repository).existsByEmail(command.email());
        verify(repository).existsByDocument(command.document());
        verify(repository).create(any(Client.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail já está em uso")
    void shouldThrowExceptionWhenEmailExists() {
        when(repository.existsByEmail(command.email())).thenReturn(true);

        assertThrows(ClientExceptions.EmailExists.class, () -> createClientUseCase.execute(command));

        verify(repository).existsByEmail(command.email());
        verify(repository, never()).create(any(Client.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o Documento já está em uso")
    void shouldThrowExceptionWhenDocumentExists() {
        when(repository.existsByEmail(command.email())).thenReturn(false);
        when(repository.existsByDocument(command.document())).thenReturn(true);

        assertThrows(ClientExceptions.DocumentExists.class, () -> createClientUseCase.execute(command));

        verify(repository).existsByDocument(command.document());
        verify(repository, never()).create(any(Client.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome do cliente é vazio")
    void shouldThrowExceptionWhenNameIsEmpty() {
        CreateClientCommand invalidCommand = new CreateClientCommand(
                "",
                "test@example.com",
                "17871234053",
                "48999999999"
        );
        when(repository.existsByEmail(invalidCommand.email())).thenReturn(false);
        when(repository.existsByDocument(invalidCommand.document())).thenReturn(false);

        assertThrows(ClientExceptions.NameNotEmpty.class, () -> createClientUseCase.execute(invalidCommand));

        verify(repository).existsByEmail(invalidCommand.email());
        verify(repository).existsByDocument(invalidCommand.document());
        verify(repository, never()).create(any(Client.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail do cliente é vazio")
    void shouldThrowExceptionWhenEmailIsEmpty() {
        CreateClientCommand invalidCommand = new CreateClientCommand(
                "Test Client",
                "",
                "17871234053",
                "48999999999"
        );
        when(repository.existsByEmail(invalidCommand.email())).thenReturn(false);
        when(repository.existsByDocument(invalidCommand.document())).thenReturn(false);

        assertThrows(ClientExceptions.EmailNotEmpty.class, () -> createClientUseCase.execute(invalidCommand));

        verify(repository).existsByDocument(invalidCommand.document());
        verify(repository, never()).create(any(Client.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o Documento do cliente é vazio")
    void shouldThrowExceptionWhenDocumentIsEmpty() {
        CreateClientCommand invalidCommand = new CreateClientCommand(
                "Test Client",
                "test@example.com",
                "",
                "48999999999"
        );
        when(repository.existsByEmail(invalidCommand.email())).thenReturn(false);
        when(repository.existsByDocument(invalidCommand.document())).thenReturn(false);

        assertThrows(ClientExceptions.DocumentNotEmpty.class, () -> createClientUseCase.execute(invalidCommand));

        verify(repository).existsByEmail(invalidCommand.email());
        verify(repository, never()).create(any(Client.class));
    }
}
