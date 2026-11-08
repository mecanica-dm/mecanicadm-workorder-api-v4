package com.mecanicadm.workorder_api.core.client.domain.port;

public record ClientPageQuery(
        ClientFilter filter,
        int page,
        int size,
        String sortBy,
        String direction
) {
}
