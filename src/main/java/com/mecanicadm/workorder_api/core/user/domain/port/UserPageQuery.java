package com.mecanicadm.workorder_api.core.user.domain.port;

public record UserPageQuery(UserFilter filter, int page, int size, String sortBy, String direction) {
}
