package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.core.user.usecase.query.FindUserByIdQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

public class GetUserByIdUseCase implements UseCase<FindUserByIdQuery, User> {

    private final UserGateway userGateway;

    public GetUserByIdUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public User execute(FindUserByIdQuery query) {
        return userGateway.findById(query.id())
                .orElseThrow(UserExceptions.NotFound::new);
    }
}
