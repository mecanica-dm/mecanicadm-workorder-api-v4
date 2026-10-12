package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderLaborItemByIdQuery;

public class GetWorkOrderLaborItemByIdUseCase implements UseCase<GetWorkOrderLaborItemByIdQuery, WorkOrderLaborItem> {
    private final WorkOrderGateway gateway;

    public GetWorkOrderLaborItemByIdUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public WorkOrderLaborItem execute(GetWorkOrderLaborItemByIdQuery query) {
        var workOrder = gateway.findByIdWithItems(query.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        return workOrder.findLaborItem(query.laborItemId())
                .orElseThrow(WorkOrderExceptions.LaborItemNotFound::new);
    }
}
