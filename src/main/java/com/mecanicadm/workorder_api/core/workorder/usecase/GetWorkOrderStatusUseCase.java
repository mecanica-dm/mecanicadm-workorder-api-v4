package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderStatusQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

public class GetWorkOrderStatusUseCase implements UseCase<GetWorkOrderStatusQuery, WorkOrderStatus> {

    private final WorkOrderGateway gateway;

    public GetWorkOrderStatusUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public WorkOrderStatus execute(GetWorkOrderStatusQuery query) {
        return gateway.findStatusById(query.id())
                .orElseThrow(WorkOrderExceptions.NotFound::new);
    }
}
