package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderByIdQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWorkOrderByIdUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private GetWorkOrderByIdUseCase useCase;

    @Test
    @DisplayName("Deve retornar a ordem de serviço quando encontrada")
    void shouldReturnWorkOrderWhenFound() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        WorkOrder result = useCase.execute(new GetWorkOrderByIdQuery(workOrderId));

        assertNotNull(result);
        assertEquals(workOrder, result);
        verify(gateway).findById(workOrderId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new GetWorkOrderByIdQuery(workOrderId)));

        verify(gateway).findById(workOrderId);
    }
}
