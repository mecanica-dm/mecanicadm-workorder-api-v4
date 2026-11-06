package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderVehicleReleasedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.ReleaseWorkOrderVehicleCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mecanicadm.workorder_api.core.workorder.usecase.SagaCommandUseCaseTestSupport.givenStored;
import static com.mecanicadm.workorder_api.core.workorder.usecase.SagaCommandUseCaseTestSupport.workOrderIn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReleaseWorkOrderVehicleUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;
    @Mock
    private MessagePublisher publisher;

    private ReleaseWorkOrderVehicleUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReleaseWorkOrderVehicleUseCase(gateway, publisher);
    }

    @Test
    @DisplayName("Deve liberar o veiculo (PAID) e publicar o evento, sem entregar")
    void shouldReleaseVehicleAndPublishEventWithoutDelivering() {
        WorkOrder workOrder = workOrderIn("EXECUTION_COMPLETED");
        givenStored(gateway, workOrder);

        useCase.execute(new ReleaseWorkOrderVehicleCommand(workOrder.getId()));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(org.mockito.ArgumentMatchers.any(), captor.capture());
        assertInstanceOf(WorkOrderVehicleReleasedEvent.class, captor.getValue());
        assertEquals(WorkOrderStatus.PAID, workOrder.getStatus());
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve ser idempotente: OS ja liberada nao gera novo evento")
    void shouldDoNothingWhenAlreadyReleased() {
        WorkOrder workOrder = workOrderIn("PAID");
        givenStored(gateway, workOrder);

        useCase.execute(new ReleaseWorkOrderVehicleCommand(workOrder.getId()));

        verify(publisher, never()).publish(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(gateway, never()).update(any());
    }

    @Test
    @DisplayName("Deve recusar a liberacao antes de a execucao ser concluida")
    void shouldRejectWhenExecutionNotCompleted() {
        WorkOrder workOrder = workOrderIn("IN_EXECUTION");
        givenStored(gateway, workOrder);

        assertThrows(WorkOrderExceptions.InvalidStatusTransition.class,
                () -> useCase.execute(new ReleaseWorkOrderVehicleCommand(workOrder.getId())));

        verify(publisher, never()).publish(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
