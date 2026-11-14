package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderStatusChange;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderStatusHistoryGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderHistoryQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import java.util.List;

public class GetWorkOrderHistoryUseCase implements UseCase<GetWorkOrderHistoryQuery, List<WorkOrderStatusChange>> {

    private final WorkOrderGateway workOrderGateway;
    private final WorkOrderStatusHistoryGateway historyGateway;

    public GetWorkOrderHistoryUseCase(WorkOrderGateway workOrderGateway, WorkOrderStatusHistoryGateway historyGateway) {
        this.workOrderGateway = workOrderGateway;
        this.historyGateway = historyGateway;
    }

    @Override
    public List<WorkOrderStatusChange> execute(GetWorkOrderHistoryQuery query) {
        if (!workOrderGateway.existsById(query.id())) {
            throw new WorkOrderExceptions.NotFound();
        }
        return historyGateway.findByWorkOrderId(query.id());
    }
}
