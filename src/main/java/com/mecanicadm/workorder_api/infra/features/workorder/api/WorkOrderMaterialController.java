package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.usecase.AddMaterialToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.RemoveMaterialItemFromWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddMaterialToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveMaterialItemFromWorkOrderCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.AddMaterialToWorkOrderRequest;
import com.mecanicadm.workorder_api.infra.features.workorder.api.openapi.WorkOrderMaterialOpenApi;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/work-orders/{workOrderId}/materials")
public class WorkOrderMaterialController implements WorkOrderMaterialOpenApi {

    private final AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;
    private final RemoveMaterialItemFromWorkOrderUseCase removeMaterialItemFromWorkOrderUseCase;

    public WorkOrderMaterialController(AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase,
                                       RemoveMaterialItemFromWorkOrderUseCase removeMaterialItemFromWorkOrderUseCase) {
        this.addMaterialToWorkOrderUseCase = addMaterialToWorkOrderUseCase;
        this.removeMaterialItemFromWorkOrderUseCase = removeMaterialItemFromWorkOrderUseCase;
    }

    @Override
    @PostMapping("/{materialId}/add")
    public ResponseEntity<Void> addMaterial(@PathVariable UUID workOrderId, @PathVariable UUID materialId, @Valid @RequestBody AddMaterialToWorkOrderRequest request) {
        addMaterialToWorkOrderUseCase.execute(new AddMaterialToWorkOrderCommand(workOrderId, materialId, request.quantity()));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{materialId}/remove")
    public ResponseEntity<Void> removeMaterial(@PathVariable UUID workOrderId, @PathVariable UUID materialId) {
        removeMaterialItemFromWorkOrderUseCase.execute(new RemoveMaterialItemFromWorkOrderCommand(workOrderId, materialId));
        return ResponseEntity.noContent().build();
    }
}