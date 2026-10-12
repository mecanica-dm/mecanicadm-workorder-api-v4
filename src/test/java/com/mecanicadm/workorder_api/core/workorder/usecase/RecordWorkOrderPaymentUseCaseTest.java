package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RecordWorkOrderPaymentCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordWorkOrderPaymentUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private RecordWorkOrderPaymentUseCase useCase;

    @Test
    @DisplayName("Deve registrar o pagamento da ordem de serviço com sucesso")
    void shouldRecordPaymentSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        useCase.execute(new RecordWorkOrderPaymentCommand(workOrderId));

        verify(workOrder).markAsPaid();
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new RecordWorkOrderPaymentCommand(workOrderId)));

        verify(gateway, never()).update(any());
    }
}
