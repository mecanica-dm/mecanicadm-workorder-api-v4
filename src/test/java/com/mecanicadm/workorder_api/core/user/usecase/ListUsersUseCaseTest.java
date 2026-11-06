package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.domain.port.UserPageQuery;
import com.mecanicadm.workorder_api.core.user.domain.port.UserPageResult;
import com.mecanicadm.workorder_api.core.user.usecase.command.ListUsersCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListUsersUseCaseTest {

    @Mock
    private UserGateway userGateway;

    private ListUsersUseCase listUsersUseCase;

    @BeforeEach
    void setUp() {
        listUsersUseCase = new ListUsersUseCase(userGateway);
    }

    @Test
    @DisplayName("Deve listar os usuários paginados com sucesso")
    void shouldListUsersPaginatedSuccessfully() {
        var command = new ListUsersCommand(0, 10, "test", "test@test.com", "name", "asc");
        var users = List.of(User.restore(UUID.randomUUID(), "test@test.com", "123", "test", null, null, LocalDateTime.now(), LocalDateTime.now()));
        var expectedResult = new UserPageResult(users, 1);

        when(userGateway.findAll(any(UserPageQuery.class))).thenReturn(expectedResult);

        var result = listUsersUseCase.execute(command);

        assertEquals(expectedResult, result);
        verify(userGateway).findAll(any(UserPageQuery.class));
    }
}
