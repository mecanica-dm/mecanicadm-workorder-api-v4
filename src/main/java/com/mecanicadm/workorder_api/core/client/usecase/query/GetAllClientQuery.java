package com.mecanicadm.workorder_api.core.client.usecase.query;

public record GetAllClientQuery(
        String name,
        String document,
        int page,
        int size,
        String sortBy,
        String direction
) {
}
