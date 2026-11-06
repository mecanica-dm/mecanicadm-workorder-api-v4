package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.usecase.AddLaborToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.FinishLaborExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.GetWorkOrderLaborItemByIdUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.RemoveLaborItemFromWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.StartLaborExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddLaborToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishLaborExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RemoveLaborItemFromWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartLaborExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderLaborItemByIdQuery;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderLaborItemResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.api.openapi.WorkOrderLaborOpenApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/work-orders/{workOrderId}/labors")
public class WorkOrderLaborController implements WorkOrderLaborOpenApi {

    private final StartLaborExecutionUseCase startLaborExecutionUseCase;
    private final FinishLaborExecutionUseCase finishLaborExecutionUseCase;
    private final GetWorkOrderLaborItemByIdUseCase getWorkOrderLaborItemByIdUseCase;
    private final AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;
    private final RemoveLaborItemFromWorkOrderUseCase removeLaborItemFromWorkOrderUseCase;

    public WorkOrderLaborController(StartLaborExecutionUseCase startLaborExecutionUseCase,
                                    FinishLaborExecutionUseCase finishLaborExecutionUseCase,
                                    GetWorkOrderLaborItemByIdUseCase getWorkOrderLaborItemByIdUseCase,
                                    AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase,
                                    RemoveLaborItemFromWorkOrderUseCase removeLaborItemFromWorkOrderUseCase) {
        this.startLaborExecutionUseCase = startLaborExecutionUseCase;
        this.finishLaborExecutionUseCase = finishLaborExecutionUseCase;
        this.getWorkOrderLaborItemByIdUseCase = getWorkOrderLaborItemByIdUseCase;
        this.addLaborToWorkOrderUseCase = addLaborToWorkOrderUseCase;
        this.removeLaborItemFromWorkOrderUseCase = removeLaborItemFromWorkOrderUseCase;
    }


    @Override
    @PostMapping("/{laborId}/add")
    public ResponseEntity<Void> addLabor(@PathVariable UUID workOrderId, @PathVariable UUID laborId) {
        addLaborToWorkOrderUseCase.execute(new AddLaborToWorkOrderCommand(workOrderId, laborId));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{laborItemId}/remove")
    public ResponseEntity<Void> removeLabor(@PathVariable UUID workOrderId, @PathVariable UUID laborItemId) {
        removeLaborItemFromWorkOrderUseCase.execute(new RemoveLaborItemFromWorkOrderCommand(workOrderId, laborItemId));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{laborItemId}/start")
    public ResponseEntity<Void> startLabor(@PathVariable UUID workOrderId, @PathVariable UUID laborItemId) {
        startLaborExecutionUseCase.execute(new StartLaborExecutionCommand(workOrderId, laborItemId));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{laborItemId}/finish")
    public ResponseEntity<Void> finishLabor(@PathVariable UUID workOrderId, @PathVariable UUID laborItemId) {
        finishLaborExecutionUseCase.execute(new FinishLaborExecutionCommand(workOrderId, laborItemId));
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/{laborItemId}")
    public ResponseEntity<WorkOrderLaborItemResponse> findById(@PathVariable UUID workOrderId, @PathVariable UUID laborItemId) {
        WorkOrderLaborItem laborItem = getWorkOrderLaborItemByIdUseCase.execute(new GetWorkOrderLaborItemByIdQuery(workOrderId, laborItemId));
        return ResponseEntity.ok(WorkOrderLaborItemResponse.from(laborItem));
    }
}
