package com.mecanicadm.workorder_api.core.material.domain.port;

public record MaterialPageQuery(
        MaterialFilter filter,
        int page,
        int size,
        String sortBy,
        String direction
) {
}
