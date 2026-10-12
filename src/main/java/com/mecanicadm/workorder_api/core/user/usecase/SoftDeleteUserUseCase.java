package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.core.user.usecase.command.SoftDeleteUserCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class SoftDeleteUserUseCase implements VoidUseCase<SoftDeleteUserCommand> {

    private final UserGateway userGateway;

    public SoftDeleteUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public void execute(SoftDeleteUserCommand command) {
        User user = userGateway.findById(command.id()).orElseThrow(UserExceptions.NotFound::new);
        user.softDelete();
        userGateway.update(user);
    }
}
