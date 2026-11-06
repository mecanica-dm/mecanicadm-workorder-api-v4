package com.mecanicadm.workorder_api.core.workorder.domain.port;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;

import java.util.List;

public record WorkOrderPageResult(
        List<WorkOrder> items, long totalElements
) {
}
