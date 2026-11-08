package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.core.user.usecase.query.FindUserByIdQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByIdUseCaseTest {

    @Mock
    private UserGateway gateway;

    private GetUserByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetUserByIdUseCase(gateway);
    }

    @Test
    @DisplayName("Deve encontrar um usuário por ID com sucesso")
    void shouldFindUserById() {
        UUID id = UUID.randomUUID();
        FindUserByIdQuery query = new FindUserByIdQuery(id);
        User user = mock(User.class);

        when(gateway.findById(id)).thenReturn(Optional.of(user));

        User result = useCase.execute(query);

        assertNotNull(result);
        assertEquals(user, result);
        verify(gateway).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não existe")
    void shouldThrowExceptionWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        FindUserByIdQuery query = new FindUserByIdQuery(id);

        when(gateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserExceptions.NotFound.class, () -> useCase.execute(query));
        verify(gateway).findById(id);
    }
}
