package com.mecanicadm.workorder_api.core.workorder.usecase.query;

import java.util.UUID;

public record GetAllWorkOrdersQuery(
        UUID clientId,
        String licensePlate,
        int page,
        int size,
        String sortBy,
        String direction
) {
}
