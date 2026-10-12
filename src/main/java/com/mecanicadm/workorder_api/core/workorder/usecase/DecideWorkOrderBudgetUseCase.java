package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DecideWorkOrderBudgetCommand;
import org.springframework.util.StringUtils;

public class DecideWorkOrderBudgetUseCase implements VoidUseCase<DecideWorkOrderBudgetCommand> {
    private final WorkOrderGateway gateway;

    public DecideWorkOrderBudgetUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(DecideWorkOrderBudgetCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        WorkOrderBudget budget = workOrder.getBudget()
                .orElseThrow(WorkOrderExceptions.BudgetNotFound::new);

        if (budget.getStatus() != WorkOrderBudgetStatus.WAITING_DECISION) {
            throw new WorkOrderExceptions.BudgetNotWaitingDecision();
        }

        processDecision(workOrder, budget, cmd);
        gateway.saveBudget(budget);
        gateway.update(workOrder);
    }

    private void processDecision(WorkOrder workOrder, WorkOrderBudget budget, DecideWorkOrderBudgetCommand cmd) {
        switch (cmd.decision()) {
            case APPROVED -> {
                budget.approve(cmd.observation());
                workOrder.markAsAwaitingExecution();
            }
            case REJECTED -> {
                validateObservation(cmd.observation());
                budget.reject(cmd.observation(), false);
                workOrder.cancel();
            }
            case CHANGES_REQUESTED -> {
                validateObservation(cmd.observation());
                budget.reject(cmd.observation(), true);
                workOrder.markAsChangesRequested();
            }
            default -> throw new WorkOrderExceptions.BudgetDecisionInvalid(cmd.decision().name());
        }
    }

    private void validateObservation(String observation) {
        if (!StringUtils.hasText(observation)) {
            throw new WorkOrderExceptions.BudgetObservationRequired();
        }
    }
}
