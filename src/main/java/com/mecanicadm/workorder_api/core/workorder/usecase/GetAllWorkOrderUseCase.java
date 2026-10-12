package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.*;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetAllWorkOrdersQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GetAllWorkOrderUseCase implements UseCase<GetAllWorkOrdersQuery, WorkOrderPageResult> {

    private static final Set<WorkOrderStatus> DEFAULT_STATUSES = Set.of(
            WorkOrderStatus.IN_EXECUTION,
            WorkOrderStatus.AWAITING_EXECUTION,
            WorkOrderStatus.DIAGNOSED,
            WorkOrderStatus.RECEIVED
    );

    private final WorkOrderGateway gateway;

    public GetAllWorkOrderUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public WorkOrderPageResult execute(GetAllWorkOrdersQuery query) {
        WorkOrderFilter filter = new WorkOrderFilter(query.clientId(), query.licensePlate(), DEFAULT_STATUSES);

        List<SortCriteria> sorts = new ArrayList<>();

        if (query.sortBy() == null && query.direction() == null) {
            sorts.add(new SortCriteria("status", "ASC"));
            sorts.add(new SortCriteria("dateCreated", "ASC"));
        } else {
            sorts.add(new SortCriteria(query.sortBy(), query.direction()));
        }

        WorkOrderPageQuery pageQuery = new WorkOrderPageQuery(
                filter, query.page(), query.size(), sorts);
        return gateway.findAll(pageQuery);
    }
}
