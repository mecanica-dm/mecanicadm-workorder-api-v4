package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderStatusChange;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.usecase.GetWorkOrderHistoryUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderHistoryQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorkOrderHistoryControllerTest {

    private final GetWorkOrderHistoryUseCase useCase = mock(GetWorkOrderHistoryUseCase.class);
    private final WorkOrderHistoryController controller = new WorkOrderHistoryController(useCase);

    @Test
    @DisplayName("Deve responder 200 com cada mudanca de status, na ordem recebida do caso de uso")
    void shouldReturnHistory() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        when(useCase.execute(new GetWorkOrderHistoryQuery(id))).thenReturn(List.of(
                new WorkOrderStatusChange(null, WorkOrderStatus.RECEIVED, null, "maria@teste.com", now),
                new WorkOrderStatusChange(WorkOrderStatus.RECEIVED, WorkOrderStatus.CHANGES_REQUESTED, "Trocar o filtro", "saga", now)));

        var response = controller.findHistory(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = response.getBody();
        assertEquals(2, body.size());
        assertNull(body.get(0).from());
        assertEquals(WorkOrderStatus.RECEIVED, body.get(0).to());
        assertEquals("maria@teste.com", body.get(0).changedBy());
        assertEquals(WorkOrderStatus.CHANGES_REQUESTED, body.get(1).to());
        assertEquals("Trocar o filtro", body.get(1).reason());
        assertEquals("saga", body.get(1).changedBy());
        assertEquals(now, body.get(1).changedAt());
    }
}
