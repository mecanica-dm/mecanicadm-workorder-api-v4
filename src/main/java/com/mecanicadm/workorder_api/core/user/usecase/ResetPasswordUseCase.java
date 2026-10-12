package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.PasswordResetToken;
import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.PasswordResetTokenGateway;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.core.user.usecase.command.ResetPasswordCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class ResetPasswordUseCase implements VoidUseCase<ResetPasswordCommand> {

    private final UserGateway userGateway;
    private final PasswordResetTokenGateway passwordResetTokenGateway;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordUseCase(UserGateway userGateway,
                                PasswordResetTokenGateway passwordResetTokenGateway,
                                PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordResetTokenGateway = passwordResetTokenGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(ResetPasswordCommand cmd) {
        PasswordResetToken resetToken = passwordResetTokenGateway.findByToken(cmd.token())
                .orElseThrow(UserExceptions.TokenInvalid::new);

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UserExceptions.TokenExpired();
        }

        User user = resetToken.getUser();
        user.updatePassword(passwordEncoder.encode(cmd.newPassword()));

        userGateway.update(user);
        passwordResetTokenGateway.delete(resetToken);
    }
}
