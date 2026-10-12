package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderStatusResponseTest {

    @Test
    @DisplayName("Deve criar resposta com ID e status corretos")
    void shouldCreateWithIdAndStatus() {
        UUID id = UUID.randomUUID();
        var response = WorkOrderStatusResponse.from(id, WorkOrderStatus.IN_EXECUTION);
        assertEquals(id, response.id());
        assertEquals(WorkOrderStatus.IN_EXECUTION, response.status());
    }

    @Test
    @DisplayName("Deve criar resposta para cada status possível")
    void shouldCreateForAllStatuses() {
        for (WorkOrderStatus status : WorkOrderStatus.values()) {
            var response = WorkOrderStatusResponse.from(UUID.randomUUID(), status);
            assertNotNull(response);
            assertEquals(status, response.status());
        }
    }
}
