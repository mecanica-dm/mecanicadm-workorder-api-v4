package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;

import java.util.UUID;

public record WorkOrderMaterialItemResponse(
        UUID materialId,
        int quantity
) {
    public static WorkOrderMaterialItemResponse from(WorkOrderMaterialItem materialItem) {
        return new WorkOrderMaterialItemResponse(
                materialItem.getMaterialId(),
                materialItem.getQuantity()
        );
    }
}
