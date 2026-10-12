package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request;

import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderMaterialItemCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateWorkOrderRequest(
        @NotNull(message = "{validation.workorder.clientId.required}")
        UUID clientId,

        @NotBlank(message = "{validation.workorder.vehicleId.required}")
        String vehicleId,

        @NotBlank(message = "{validation.workorder.description.required}")
        String description,

        List<UUID> laborIds,

        @Valid
        List<CreateWorkOrderMaterialItemRequest> materialItems
) {
    public CreateWorkOrderCommand toCommand() {
        var materialCommands = materialItems != null
                ? materialItems.stream()
                    .map(m -> new CreateWorkOrderMaterialItemCommand(null, m.materialId(), m.quantity()))
                    .toList()
                : null;
        return new CreateWorkOrderCommand(clientId, vehicleId, description, laborIds, materialCommands);
    }
}
