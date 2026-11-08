package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderPageResult;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetAllWorkOrdersQuery;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public class WorkOrderQueryMapper {

    private WorkOrderQueryMapper() {
    }

    public static GetAllWorkOrdersQuery toQuery(UUID clientId, String licensePlate, Pageable pageable) {
        var sort = pageable.getSort().stream().findFirst();
        return new GetAllWorkOrdersQuery(
                clientId,
                licensePlate,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort.map(Sort.Order::getProperty).orElse(null),
                sort.map(Sort.Order::getDirection).map(Enum::name).orElse(null)
        );
    }

    public static Page<WorkOrderResponse> toPage(WorkOrderPageResult result, Pageable pageable) {
        var items = result.items().stream().map(WorkOrderResponse::from).toList();
        return new PageImpl<>(items, pageable, result.totalElements());
    }
}
