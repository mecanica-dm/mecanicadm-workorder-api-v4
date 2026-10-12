package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.PasswordResetToken;
import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.PasswordResetTokenGateway;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.usecase.command.RequestPasswordResetCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class RequestPasswordResetUseCase implements VoidUseCase<RequestPasswordResetCommand> {

    private static final Logger logger = LoggerFactory.getLogger(RequestPasswordResetUseCase.class);

    private final UserGateway userGateway;
    private final PasswordResetTokenGateway passwordResetTokenGateway;

    public RequestPasswordResetUseCase(UserGateway userGateway,
                                       PasswordResetTokenGateway passwordResetTokenGateway) {
        this.userGateway = userGateway;
        this.passwordResetTokenGateway = passwordResetTokenGateway;
    }

    @Override
    public void execute(RequestPasswordResetCommand cmd) {
        Optional<User> userOptional = userGateway.findByEmail(cmd.email());
        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        passwordResetTokenGateway.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user, LocalDateTime.now().plusMinutes(10));
        passwordResetTokenGateway.save(myToken);

        logger.info("""
                            ==========================================================
                            RECUPERAÇÃO DE SENHA SOLICITADA
                            E-mail: {}
                            Token: {}
                            ==========================================================
                        """,
                cmd.email(), token);
    }
}
