package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RecordWorkOrderPaymentCommand;

public class RecordWorkOrderPaymentUseCase implements VoidUseCase<RecordWorkOrderPaymentCommand> {
    private final WorkOrderGateway gateway;

    public RecordWorkOrderPaymentUseCase(WorkOrderGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(RecordWorkOrderPaymentCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.markAsPaid();
        gateway.update(workOrder);
    }
}
