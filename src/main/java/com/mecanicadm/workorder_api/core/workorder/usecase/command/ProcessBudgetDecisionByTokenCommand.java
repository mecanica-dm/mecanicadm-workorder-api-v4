package com.mecanicadm.workorder_api.core.workorder.usecase.command;

public record ProcessBudgetDecisionByTokenCommand(String token, String action, String observation) {
}
