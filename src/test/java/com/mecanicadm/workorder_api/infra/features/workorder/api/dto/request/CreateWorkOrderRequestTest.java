package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request;

import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateWorkOrderRequestTest {

    @Test
    @DisplayName("Deve converter para CreateWorkOrderCommand com material items")
    void shouldConvertToCommandWithMaterialItems() {
        UUID clientId = UUID.randomUUID();
        UUID materialId1 = UUID.randomUUID();
        UUID materialId2 = UUID.randomUUID();
        var request = new CreateWorkOrderRequest(
                clientId, "ABC-1234", "Troca de oleo",
                List.of(UUID.randomUUID()),
                List.of(
                        new CreateWorkOrderMaterialItemRequest(materialId1, 2),
                        new CreateWorkOrderMaterialItemRequest(materialId2, 1)
                )
        );

        CreateWorkOrderCommand command = request.toCommand();

        assertEquals(clientId, command.clientId());
        assertEquals("ABC-1234", command.vehicleId());
        assertEquals("Troca de oleo", command.description());
        assertNotNull(command.laborIds());
        assertEquals(2, command.materialItems().size());
        assertEquals(materialId1, command.materialItems().getFirst().materialId());
        assertEquals(2, command.materialItems().getFirst().quantity());
    }

    @Test
    @DisplayName("Deve converter para CreateWorkOrderCommand sem material items")
    void shouldConvertToCommandWithoutMaterialItems() {
        UUID clientId = UUID.randomUUID();
        var request = new CreateWorkOrderRequest(clientId, "DEF-5678", "Alinhamento", null, null);

        CreateWorkOrderCommand command = request.toCommand();

        assertEquals(clientId, command.clientId());
        assertEquals("DEF-5678", command.vehicleId());
        assertNull(command.materialItems());
    }
}
