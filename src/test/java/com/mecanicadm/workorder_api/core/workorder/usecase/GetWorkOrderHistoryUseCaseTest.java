package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderStatusChange;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderStatusHistoryGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderHistoryQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetWorkOrderHistoryUseCaseTest {

    private final WorkOrderGateway workOrderGateway = mock(WorkOrderGateway.class);
    private final WorkOrderStatusHistoryGateway historyGateway = mock(WorkOrderStatusHistoryGateway.class);
    private final GetWorkOrderHistoryUseCase useCase = new GetWorkOrderHistoryUseCase(workOrderGateway, historyGateway);

    @Test
    @DisplayName("Deve devolver o historico da OS na ordem em que o gateway entrega")
    void shouldReturnHistory() {
        UUID id = UUID.randomUUID();
        List<WorkOrderStatusChange> history = List.of(
                new WorkOrderStatusChange(null, WorkOrderStatus.RECEIVED, null, "maria@teste.com", LocalDateTime.now()),
                new WorkOrderStatusChange(WorkOrderStatus.RECEIVED, WorkOrderStatus.DIAGNOSED, null, "maria@teste.com", LocalDateTime.now()));
        when(workOrderGateway.existsById(id)).thenReturn(true);
        when(historyGateway.findByWorkOrderId(id)).thenReturn(history);

        assertEquals(history, useCase.execute(new GetWorkOrderHistoryQuery(id)));
    }

    @Test
    @DisplayName("Deve lancar NotFound, sem consultar o historico, quando a OS nao existir")
    void shouldThrowWhenWorkOrderDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(workOrderGateway.existsById(id)).thenReturn(false);

        assertThrows(WorkOrderExceptions.NotFound.class, () -> useCase.execute(new GetWorkOrderHistoryQuery(id)));

        verify(historyGateway, never()).findByWorkOrderId(id);
    }
}
