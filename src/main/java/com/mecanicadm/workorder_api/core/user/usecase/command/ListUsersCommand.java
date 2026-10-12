package com.mecanicadm.workorder_api.core.user.usecase.command;

public record ListUsersCommand(int page, int size, String name, String email, String sortBy, String direction) {
}
