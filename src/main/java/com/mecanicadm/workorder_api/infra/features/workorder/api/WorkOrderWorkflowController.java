package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.usecase.DeliverWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.DiagnoseWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.FinishWorkOrderExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.StartWorkOrderExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DeliverWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DiagnoseWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishWorkOrderExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartWorkOrderExecutionCommand;
import com.mecanicadm.workorder_api.infra.features.workorder.api.openapi.WorkOrderWorkflowOpenApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/work-orders/{id}/step/")
public class WorkOrderWorkflowController implements WorkOrderWorkflowOpenApi {

    private final DiagnoseWorkOrderUseCase diagnoseWorkOrderUseCase;
    private final StartWorkOrderExecutionUseCase startWorkOrderExecutionUseCase;
    private final FinishWorkOrderExecutionUseCase finishWorkOrderExecutionUseCase;
    private final DeliverWorkOrderUseCase deliverWorkOrderUseCase;

    public WorkOrderWorkflowController(DiagnoseWorkOrderUseCase diagnoseWorkOrderUseCase,
                                       StartWorkOrderExecutionUseCase startWorkOrderExecutionUseCase,
                                       FinishWorkOrderExecutionUseCase finishWorkOrderExecutionUseCase,
                                       DeliverWorkOrderUseCase deliverWorkOrderUseCase) {
        this.diagnoseWorkOrderUseCase = diagnoseWorkOrderUseCase;
        this.startWorkOrderExecutionUseCase = startWorkOrderExecutionUseCase;
        this.finishWorkOrderExecutionUseCase = finishWorkOrderExecutionUseCase;
        this.deliverWorkOrderUseCase = deliverWorkOrderUseCase;
    }

    @Override
    @PostMapping("/diagnose")
    public ResponseEntity<UUID> diagnose(@PathVariable UUID id) {
        UUID workOrderId = diagnoseWorkOrderUseCase.execute(new DiagnoseWorkOrderCommand(id));
        return ResponseEntity.ok(workOrderId);
    }

    @Override
    @PostMapping("/start-execution")
    public ResponseEntity<Void> startExecution(@PathVariable UUID id) {
        startWorkOrderExecutionUseCase.execute(new StartWorkOrderExecutionCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/finish-execution")
    public ResponseEntity<Void> finishExecution(@PathVariable UUID id) {
        finishWorkOrderExecutionUseCase.execute(new FinishWorkOrderExecutionCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/deliver")
    public ResponseEntity<Void> deliver(@PathVariable UUID id) {
        deliverWorkOrderUseCase.execute(new DeliverWorkOrderCommand(id));
        return ResponseEntity.noContent().build();
    }
}

