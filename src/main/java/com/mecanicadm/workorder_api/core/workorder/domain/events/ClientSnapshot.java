package com.mecanicadm.workorder_api.core.workorder.domain.events;

import java.util.UUID;

public record ClientSnapshot(
        UUID id,
        String name,
        String email,
        String document,
        String phone) {
}
