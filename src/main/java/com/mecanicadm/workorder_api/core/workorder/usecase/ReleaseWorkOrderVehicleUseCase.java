package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderVehicleReleasedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.ReleaseWorkOrderVehicleCommand;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;

public class ReleaseWorkOrderVehicleUseCase implements VoidUseCase<ReleaseWorkOrderVehicleCommand> {
    private final WorkOrderGateway gateway;
    private final MessagePublisher publisher;

    public ReleaseWorkOrderVehicleUseCase(WorkOrderGateway gateway, MessagePublisher publisher) {
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void execute(ReleaseWorkOrderVehicleCommand command) {
        WorkOrder workOrder = gateway.findById(command.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        if (workOrder.getStatus() == WorkOrderStatus.PAID) {
            return;
        }

        workOrder.releaseForDelivery();

        gateway.update(workOrder);
        publisher.publish(workOrder.getId(), WorkOrderVehicleReleasedEvent.of(workOrder));
    }
}
