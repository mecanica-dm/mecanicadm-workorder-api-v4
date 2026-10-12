package com.mecanicadm.workorder_api.core.labor.usecase.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateLaborCommand(
        UUID id,
        String name,
        BigDecimal price
) {
}
