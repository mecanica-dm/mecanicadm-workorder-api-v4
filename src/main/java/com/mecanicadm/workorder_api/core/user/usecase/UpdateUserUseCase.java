package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.core.user.usecase.command.UpdateUserCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UpdateUserUseCase implements VoidUseCase<UpdateUserCommand> {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(UpdateUserCommand command) {
        User user = userGateway.findById(command.id()).orElseThrow(UserExceptions.NotFound::new);

        if (command.name() != null && !command.name().isBlank()) {
            user.updateInfo(command.name(), user.getEmail());
        }

        if (command.password() != null && !command.password().isBlank()) {
            if (command.currentPassword() == null || command.currentPassword().isBlank()) {
                throw new UserExceptions.CurrentPasswordRequired();
            }

            if (!passwordEncoder.matches(command.currentPassword(), user.getPassword())) {
                throw new UserExceptions.BadCredentials();
            }

            User.validatePassword(command.password());
            user.updatePassword(passwordEncoder.encode(command.password()));
        }

        userGateway.update(user);
    }
}
