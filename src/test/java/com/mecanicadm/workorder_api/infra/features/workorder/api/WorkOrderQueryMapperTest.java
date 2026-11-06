package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderPageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderQueryMapperTest {

    @Test
    @DisplayName("Deve converter parâmetros de consulta para GetAllWorkOrdersQuery")
    void shouldConvertToQuery() {
        var clientId = UUID.randomUUID();
        var pageable = PageRequest.of(0, 20, Sort.by("executionStartAt").descending());

        var query = WorkOrderQueryMapper.toQuery(clientId, "ABC-1234", pageable);

        assertEquals(clientId, query.clientId());
        assertEquals("ABC-1234", query.licensePlate());
        assertEquals(0, query.page());
        assertEquals(20, query.size());
        assertEquals("executionStartAt", query.sortBy());
        assertEquals("DESC", query.direction());
    }

    @Test
    @DisplayName("Deve retornar sort nulo quando não especificado")
    void shouldReturnNullSortWhenNotProvided() {
        var pageable = PageRequest.of(1, 10);

        var query = WorkOrderQueryMapper.toQuery(null, null, pageable);

        assertEquals(1, query.page());
        assertEquals(10, query.size());
        assertNull(query.sortBy());
        assertNull(query.direction());
    }

    @Test
    @DisplayName("Deve converter WorkOrderPageResult para Page de WorkOrderResponse")
    void shouldConvertToPage() {
        var workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Troca de oleo");
        var result = new WorkOrderPageResult(List.of(workOrder), 1L);
        var pageable = PageRequest.of(0, 20);

        var page = WorkOrderQueryMapper.toPage(result, pageable);

        assertEquals(1, page.getContent().size());
        assertEquals(1, page.getTotalElements());
        assertEquals(workOrder.getId(), page.getContent().getFirst().id());
    }
}
