package com.mecanicadm.workorder_api.core.workorder.usecase.query;

import java.util.UUID;

public record GetWorkOrderLaborItemByIdQuery(UUID workOrderId, UUID laborItemId) { }
