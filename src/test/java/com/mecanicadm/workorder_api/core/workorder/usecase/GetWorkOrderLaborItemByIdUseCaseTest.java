package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderLaborItemByIdQuery;
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
class GetWorkOrderLaborItemByIdUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private GetWorkOrderLaborItemByIdUseCase useCase;

    @Test
    @DisplayName("Deve retornar o item de serviço com sucesso")
    void shouldGetLaborItemSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        WorkOrderLaborItem laborItem = mock(WorkOrderLaborItem.class);
        when(gateway.findByIdWithItems(workOrderId)).thenReturn(Optional.of(workOrder));
        when(workOrder.findLaborItem(laborItemId)).thenReturn(Optional.of(laborItem));

        WorkOrderLaborItem result = useCase.execute(new GetWorkOrderLaborItemByIdQuery(workOrderId, laborItemId));

        assertNotNull(result);
        assertSame(laborItem, result);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        when(gateway.findByIdWithItems(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new GetWorkOrderLaborItemByIdQuery(workOrderId, laborItemId)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o item de serviço não for encontrado")
    void shouldThrowExceptionWhenLaborItemNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(gateway.findByIdWithItems(workOrderId)).thenReturn(Optional.of(workOrder));
        when(workOrder.findLaborItem(laborItemId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.LaborItemNotFound.class,
                () -> useCase.execute(new GetWorkOrderLaborItemByIdQuery(workOrderId, laborItemId)));
    }
}
