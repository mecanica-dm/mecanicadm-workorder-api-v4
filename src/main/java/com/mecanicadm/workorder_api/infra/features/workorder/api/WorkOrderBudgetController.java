package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.usecase.*;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CalculateWorkOrderBudgetCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DecideWorkOrderBudgetCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.ManuallyAdjustWorkOrderBudgetCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.SendWorkOrderBudgetCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableBudgetResponse;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetPrintableBudgetQuery;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.DecideWorkOrderBudgetRequest;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.ManuallyAdjustWorkOrderBudgetRequest;
import com.mecanicadm.workorder_api.infra.features.workorder.api.openapi.WorkOrderBudgetOpenApi;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/work-orders/{workOrderId}/budget")
public class WorkOrderBudgetController implements WorkOrderBudgetOpenApi {

    private final SendWorkOrderBudgetUseCase sendWorkOrderBudgetUseCase;
    private final ManuallyAdjustWorkOrderBudgetUseCase manuallyAdjustWorkOrderBudgetUseCase;
    private final DecideWorkOrderBudgetUseCase decideWorkOrderBudgetUseCase;
    private final CalculateWorkOrderBudgetUseCase calculateWorkOrderBudgetUseCase;
    private final GetPrintableBudgetUseCase getPrintableBudgetUseCase;

    @Value("${app.budget.base-url}")
    private String baseUrl;

    public WorkOrderBudgetController(SendWorkOrderBudgetUseCase sendWorkOrderBudgetUseCase,
                                     ManuallyAdjustWorkOrderBudgetUseCase manuallyAdjustWorkOrderBudgetUseCase,
                                     DecideWorkOrderBudgetUseCase decideWorkOrderBudgetUseCase,
                                     CalculateWorkOrderBudgetUseCase calculateWorkOrderBudgetUseCase,
                                     GetPrintableBudgetUseCase getPrintableBudgetUseCase) {
        this.sendWorkOrderBudgetUseCase = sendWorkOrderBudgetUseCase;
        this.manuallyAdjustWorkOrderBudgetUseCase = manuallyAdjustWorkOrderBudgetUseCase;
        this.decideWorkOrderBudgetUseCase = decideWorkOrderBudgetUseCase;
        this.calculateWorkOrderBudgetUseCase = calculateWorkOrderBudgetUseCase;
        this.getPrintableBudgetUseCase = getPrintableBudgetUseCase;
    }

    @Override
    @PostMapping("/send")
    public ResponseEntity<Void> sendBudget(@PathVariable UUID workOrderId) {
        sendWorkOrderBudgetUseCase.execute(new SendWorkOrderBudgetCommand(workOrderId, baseUrl));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping
    public ResponseEntity<Void> adjustBudget(@PathVariable UUID workOrderId, @Valid @RequestBody ManuallyAdjustWorkOrderBudgetRequest request) {
        manuallyAdjustWorkOrderBudgetUseCase.execute(new ManuallyAdjustWorkOrderBudgetCommand(workOrderId, request.newTotalPrice()));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/recalculate")
    public ResponseEntity<Void> recalculateBudget(@PathVariable UUID workOrderId) {
        calculateWorkOrderBudgetUseCase.execute(new CalculateWorkOrderBudgetCommand(workOrderId));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/decision")
    public ResponseEntity<Void> decideBudget(@PathVariable UUID workOrderId, @Valid @RequestBody DecideWorkOrderBudgetRequest request) {
        decideWorkOrderBudgetUseCase.execute(new DecideWorkOrderBudgetCommand(workOrderId, request.decision(), request.observation()));
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/print")
    public ResponseEntity<PrintableBudgetResponse> printBudget(@PathVariable UUID workOrderId) {
        PrintableBudgetResponse response = getPrintableBudgetUseCase.execute(new GetPrintableBudgetQuery(workOrderId));
        return ResponseEntity.ok(response);
    }
}
