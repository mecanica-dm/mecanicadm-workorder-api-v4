package com.mecanicadm.workorder_api.core.workorder.domain.port;

import java.util.List;

public record WorkOrderPageQuery(
        WorkOrderFilter filter,
        int page,
        int size,
        List<SortCriteria> sorts) {
}
