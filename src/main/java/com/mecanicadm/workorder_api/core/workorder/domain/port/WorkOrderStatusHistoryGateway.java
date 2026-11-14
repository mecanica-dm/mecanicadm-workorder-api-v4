package com.mecanicadm.workorder_api.core.workorder.domain.port;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderStatusChange;

import java.util.List;
import java.util.UUID;

public interface WorkOrderStatusHistoryGateway {

    List<WorkOrderStatusChange> findByWorkOrderId(UUID workOrderId);
}
