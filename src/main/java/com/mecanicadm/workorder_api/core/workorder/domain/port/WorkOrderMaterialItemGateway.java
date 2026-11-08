package com.mecanicadm.workorder_api.core.workorder.domain.port;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;

import java.util.Optional;
import java.util.UUID;

public interface WorkOrderMaterialItemGateway {
    WorkOrderMaterialItem create(WorkOrderMaterialItem materialItem, UUID workOrderId);

    WorkOrderMaterialItem update(WorkOrderMaterialItem materialItem, UUID workOrderId);

    Optional<WorkOrderMaterialItem> findByWorkOrderIdAndMaterialId(UUID workOrderId, UUID materialId);

    void deleteByWorkOrderIdAndMaterialId(UUID workOrderId, UUID materialId);
}
