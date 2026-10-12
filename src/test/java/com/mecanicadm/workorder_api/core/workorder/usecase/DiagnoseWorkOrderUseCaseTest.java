package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DiagnoseWorkOrderCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnoseWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @Mock
    private ClientGateway clientGateway;

    @Mock
    private VehicleGateway vehicleGateway;

    @Mock
    private CalculateWorkOrderBudgetUseCase calculateWorkOrderBudgetUseCase;

    @InjectMocks
    private DiagnoseWorkOrderUseCase useCase;

    @Test
    @DisplayName("Deve diagnosticar ordem de serviço com sucesso")
    void shouldDiagnoseWorkOrderSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        String vehicleId = "ABC-1234";
        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrder.getId()).thenReturn(workOrderId);
        when(workOrder.getClientId()).thenReturn(clientId);
        when(workOrder.getVehicleId()).thenReturn(vehicleId);
        when(workOrder.getLaborItems()).thenReturn(Set.of(mock(WorkOrderLaborItem.class)));

        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(clientGateway.existsById(clientId)).thenReturn(true);
        when(vehicleGateway.existsByLicensePlate(vehicleId)).thenReturn(true);

        UUID resultId = useCase.execute(new DiagnoseWorkOrderCommand(workOrderId));

        assertEquals(workOrderId, resultId);
        verify(workOrder).markAsDiagnosed();
        verify(calculateWorkOrderBudgetUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não tiver itens de serviço")
    void shouldThrowExceptionWhenNoLaborItems() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrder.getLaborItems()).thenReturn(Set.of());

        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        assertThrows(WorkOrderExceptions.LaborItemsRequired.class,
                () -> useCase.execute(new DiagnoseWorkOrderCommand(workOrderId)));

        verify(workOrder, never()).markAsDiagnosed();
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente não for encontrado")
    void shouldThrowExceptionWhenClientNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrder.getLaborItems()).thenReturn(Set.of(mock(WorkOrderLaborItem.class)));
        when(workOrder.getClientId()).thenReturn(clientId);
        when(workOrder.getVehicleId()).thenReturn("ABC-1234");

        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(clientGateway.existsById(clientId)).thenReturn(false);

        assertThrows(ClientExceptions.NotFound.class,
                () -> useCase.execute(new DiagnoseWorkOrderCommand(workOrderId)));

        verify(workOrder, never()).markAsDiagnosed();
    }
}
