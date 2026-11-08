package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.port.UserFilter;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.domain.port.UserPageQuery;
import com.mecanicadm.workorder_api.core.user.domain.port.UserPageResult;
import com.mecanicadm.workorder_api.core.user.usecase.command.ListUsersCommand;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

public class ListUsersUseCase implements UseCase<ListUsersCommand, UserPageResult> {

    private final UserGateway gateway;

    public ListUsersUseCase(UserGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public UserPageResult execute(ListUsersCommand command) {
        var filter = new UserFilter(command.name(), command.email());
        var query = new UserPageQuery(filter, command.page(), command.size(), command.sortBy(), command.direction());
        return gateway.findAll(query);
    }
}
