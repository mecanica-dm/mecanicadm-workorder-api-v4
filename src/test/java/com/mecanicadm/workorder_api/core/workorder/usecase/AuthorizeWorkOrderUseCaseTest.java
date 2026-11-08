package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderAuthorizedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AuthorizeWorkOrderCommand;
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
class AuthorizeWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;
    @Mock
    private MessagePublisher publisher;

    private AuthorizeWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AuthorizeWorkOrderUseCase(gateway, publisher);
    }

    @Test
    @DisplayName("Deve autorizar OS diagnosticada e publicar WorkOrderAuthorizedEvent")
    void shouldAuthorizeAndPublishEventWhenDiagnosed() {
        WorkOrder workOrder = workOrderIn("DIAGNOSED");
        givenStored(gateway, workOrder);

        useCase.execute(new AuthorizeWorkOrderCommand(workOrder.getId()));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(org.mockito.ArgumentMatchers.any(), captor.capture());
        assertInstanceOf(WorkOrderAuthorizedEvent.class, captor.getValue());
        assertEquals(WorkOrderStatus.AWAITING_EXECUTION, workOrder.getStatus());
        assertEquals(workOrder.getId(), ((WorkOrderAuthorizedEvent) captor.getValue()).workOrderId());
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve recusar a autorizacao fora do estado diagnosticado, sem publicar nem persistir")
    void shouldRejectWhenNotDiagnosed() {
        WorkOrder workOrder = workOrderIn("RECEIVED");
        givenStored(gateway, workOrder);

        assertThrows(WorkOrderExceptions.InvalidStatusTransition.class,
                () -> useCase.execute(new AuthorizeWorkOrderCommand(workOrder.getId())));

        assertEquals(WorkOrderStatus.RECEIVED, workOrder.getStatus());
        verify(publisher, never()).publish(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(gateway, never()).update(any());
    }
}
