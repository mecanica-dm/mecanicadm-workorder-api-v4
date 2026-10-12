package com.mecanicadm.workorder_api.core.user.usecase.query;

import java.util.UUID;

public record FindUserByIdQuery(
        UUID id
) {
}
