package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.PasswordResetToken;
import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.PasswordResetTokenGateway;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.usecase.command.RequestPasswordResetCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestPasswordResetUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordResetTokenGateway passwordResetTokenGateway;

    private RequestPasswordResetUseCase requestPasswordResetUseCase;

    @BeforeEach
    void setUp() {
        requestPasswordResetUseCase = new RequestPasswordResetUseCase(userGateway, passwordResetTokenGateway);
    }

    @Test
    @DisplayName("Deve solicitar redefinição de senha com sucesso para e-mail existente")
    void shouldRequestResetSuccessfully() {
        String email = "test@example.com";
        User user = mock(User.class);
        when(userGateway.findByEmail(email)).thenReturn(Optional.of(user));

        requestPasswordResetUseCase.execute(new RequestPasswordResetCommand(email));

        verify(passwordResetTokenGateway).deleteByUser(user);
        verify(passwordResetTokenGateway).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Não deve fazer nada se o e-mail não existir")
    void shouldDoNothingWhenEmailDoesNotExist() {
        String email = "notfound@example.com";
        when(userGateway.findByEmail(email)).thenReturn(Optional.empty());

        requestPasswordResetUseCase.execute(new RequestPasswordResetCommand(email));

        verify(passwordResetTokenGateway, never()).deleteByUser(any());
        verify(passwordResetTokenGateway, never()).save(any());
    }
}
