package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkOrderMaterialItemResponseTest {

    @Test
    @DisplayName("Deve criar WorkOrderMaterialItemResponse a partir de WorkOrderMaterialItem via factory method")
    void shouldCreateMaterialItemResponseFromMaterialItem() {
        var id = UUID.randomUUID();
        var materialId = UUID.randomUUID();
        var materialItem = WorkOrderMaterialItem.restore(id, materialId, 5);

        WorkOrderMaterialItemResponse response = WorkOrderMaterialItemResponse.from(materialItem);

        assertEquals(materialId, response.materialId());
        assertEquals(5, response.quantity());
    }
}
