package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CalculateWorkOrderBudgetCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DiagnoseWorkOrderCommand;

import java.util.Objects;
import java.util.UUID;

public class DiagnoseWorkOrderUseCase implements UseCase<DiagnoseWorkOrderCommand, UUID> {
    private final WorkOrderGateway gateway;
    private final ClientGateway clientGateway;
    private final VehicleGateway vehicleGateway;
    private final CalculateWorkOrderBudgetUseCase calculateWorkOrderBudgetUseCase;

    public DiagnoseWorkOrderUseCase(WorkOrderGateway gateway,
                                    ClientGateway clientGateway,
                                    VehicleGateway vehicleGateway,
                                    CalculateWorkOrderBudgetUseCase calculateWorkOrderBudgetUseCase) {
        this.gateway = gateway;
        this.clientGateway = clientGateway;
        this.vehicleGateway = vehicleGateway;
        this.calculateWorkOrderBudgetUseCase = calculateWorkOrderBudgetUseCase;
    }

    public UUID execute(DiagnoseWorkOrderCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        validateDiagnosisPreconditions(workOrder);

        workOrder.markAsDiagnosed();
        gateway.update(workOrder);

        calculateWorkOrderBudgetUseCase.execute(new CalculateWorkOrderBudgetCommand(workOrder.getId()));
        return workOrder.getId();
    }

    private void validateDiagnosisPreconditions(WorkOrder workOrder) {
        if (workOrder.getLaborItems().isEmpty()) {
            throw new WorkOrderExceptions.LaborItemsRequired();
        }

        if (Objects.isNull(workOrder.getClientId())) {
            throw new WorkOrderExceptions.ClientRequired();
        }

        if (Objects.isNull(workOrder.getVehicleId()) || workOrder.getVehicleId().isBlank()) {
            throw new WorkOrderExceptions.VehicleRequired();
        }

        if (!clientGateway.existsById(workOrder.getClientId())) {
            throw new ClientExceptions.NotFound();
        }

        if (!vehicleGateway.existsByLicensePlate(workOrder.getVehicleId())) {
            throw new VehicleExceptions.NotFound();
        }
    }
}

