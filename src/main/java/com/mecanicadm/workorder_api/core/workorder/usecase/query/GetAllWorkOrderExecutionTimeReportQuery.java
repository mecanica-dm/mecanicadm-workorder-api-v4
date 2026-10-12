package com.mecanicadm.workorder_api.core.workorder.usecase.query;

import java.time.LocalDate;

public record GetAllWorkOrderExecutionTimeReportQuery(
        LocalDate initialDate,
        LocalDate finalDate
) {
}

