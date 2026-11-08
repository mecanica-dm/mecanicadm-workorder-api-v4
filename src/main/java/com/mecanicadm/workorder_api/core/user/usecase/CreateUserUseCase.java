package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.core.user.usecase.command.CreateUserCommand;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class CreateUserUseCase implements UseCase<CreateUserCommand, UUID> {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UUID execute(CreateUserCommand command) {
        if (userGateway.existsByEmail(command.email())) {
            throw new UserExceptions.UserAlreadyExists();
        }

        User.validatePassword(command.password());

        User user = User.create(
                command.email(),
                passwordEncoder.encode(command.password()),
                command.name()
        );

        User created = userGateway.create(user);
        return created.getId();
    }
}
