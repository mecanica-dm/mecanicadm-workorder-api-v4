package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderChangesRequestedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RequestWorkOrderChangesCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.mecanicadm.workorder_api.core.workorder.usecase.SagaCommandUseCaseTestSupport.givenStored;
import static com.mecanicadm.workorder_api.core.workorder.usecase.SagaCommandUseCaseTestSupport.workOrderIn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestWorkOrderChangesUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;
    @Mock
    private MessagePublisher publisher;

    private RequestWorkOrderChangesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RequestWorkOrderChangesUseCase(gateway, publisher);
    }

    @Test
    @DisplayName("Deve voltar a OS para CHANGES_REQUESTED e publicar o evento com a observacao")
    void shouldRequestChangesAndPublishEventWithObservation() {
        WorkOrder workOrder = workOrderIn("DIAGNOSED");
        givenStored(gateway, workOrder);

        useCase.execute(new RequestWorkOrderChangesCommand(workOrder.getId(), "Nao trocar filtro"));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(ArgumentMatchers.any(), captor.capture());
        WorkOrderChangesRequestedEvent event = assertInstanceOf(WorkOrderChangesRequestedEvent.class, captor.getValue());
        assertEquals("Nao trocar filtro", event.observation());
        assertEquals(WorkOrderStatus.CHANGES_REQUESTED, workOrder.getStatus());
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve salvar a observacao na ordem de servico e permitir diagnosticar de novo")
    void shouldStoreObservationAndAllowDiagnosingAgainAfterChangesRequested() {
        WorkOrder workOrder = workOrderIn("DIAGNOSED");
        givenStored(gateway, workOrder);

        useCase.execute(new RequestWorkOrderChangesCommand(workOrder.getId(), "ajustar"));
        assertEquals("ajustar", workOrder.getRequestedChanges());

        workOrder.markAsDiagnosed();

        assertEquals(WorkOrderStatus.DIAGNOSED, workOrder.getStatus());
    }

    @Test
    @DisplayName("Deve lancar excecao quando a ordem de servico nao for encontrada")
    void shouldThrowWhenWorkOrderNotFound() {
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new RequestWorkOrderChangesCommand(id, "x")));
    }
}
