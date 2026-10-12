package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderPageQuery;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderPageResult;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetAllWorkOrdersQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private GetAllWorkOrderUseCase useCase;

    @Captor
    private ArgumentCaptor<WorkOrderPageQuery> pageQueryCaptor;

    @Test
    @DisplayName("Deve retornar ordens de serviço paginadas com filtros e sempre aplicar ACTIVE_STATUSES")
    void shouldReturnPaginatedWorkOrdersWithFilters() {
        UUID clientId = UUID.randomUUID();
        String licensePlate = "ABC-1234";
        int page = 0;
        int size = 10;
        String sortBy = "dateCreated";
        String direction = "DESC";
        GetAllWorkOrdersQuery query = new GetAllWorkOrdersQuery(clientId, licensePlate, page, size, sortBy, direction);

        WorkOrderPageResult expectedResult = new WorkOrderPageResult(List.of(mock(WorkOrder.class)), 1L);
        when(gateway.findAll(any())).thenReturn(expectedResult);

        WorkOrderPageResult result = useCase.execute(query);

        assertEquals(expectedResult, result);
        verify(gateway).findAll(pageQueryCaptor.capture());

        var capturedQuery = pageQueryCaptor.getValue();
        assertFilter(capturedQuery, clientId, licensePlate);
        assertEquals(page, capturedQuery.page());
        assertEquals(size, capturedQuery.size());
        assertEquals(1, capturedQuery.sorts().size());
        assertEquals(sortBy, capturedQuery.sorts().get(0).field());
        assertEquals(direction, capturedQuery.sorts().get(0).direction());
    }

    @Test
    @DisplayName("Deve aplicar sort padrão por status ASC e dateCreated ASC quando sortBy/direction forem nulos")
    void shouldApplyDefaultSortWhenSortByAndDirectionAreNull() {
        GetAllWorkOrdersQuery query = new GetAllWorkOrdersQuery(null, null, 0, 10, null, null);

        WorkOrderPageResult expectedResult = new WorkOrderPageResult(List.of(mock(WorkOrder.class)), 1L);
        when(gateway.findAll(any())).thenReturn(expectedResult);

        useCase.execute(query);

        verify(gateway).findAll(pageQueryCaptor.capture());

        var capturedQuery = pageQueryCaptor.getValue();
        assertEquals(2, capturedQuery.sorts().size());
        assertEquals("status", capturedQuery.sorts().get(0).field());
        assertEquals("ASC", capturedQuery.sorts().get(0).direction());
        assertEquals("dateCreated", capturedQuery.sorts().get(1).field());
        assertEquals("ASC", capturedQuery.sorts().get(1).direction());
    }

    @Test
    @DisplayName("Deve respeitar sort explícito por status sem adicionar tiebreaker")
    void shouldRespectExplicitStatusSortWithoutTiebreaker() {
        GetAllWorkOrdersQuery query = new GetAllWorkOrdersQuery(null, null, 0, 10, "status", "DESC");

        WorkOrderPageResult expectedResult = new WorkOrderPageResult(List.of(mock(WorkOrder.class)), 1L);
        when(gateway.findAll(any())).thenReturn(expectedResult);

        useCase.execute(query);

        verify(gateway).findAll(pageQueryCaptor.capture());

        var capturedQuery = pageQueryCaptor.getValue();
        assertEquals(1, capturedQuery.sorts().size());
        assertEquals("status", capturedQuery.sorts().get(0).field());
        assertEquals("DESC", capturedQuery.sorts().get(0).direction());
    }

    @Test
    @DisplayName("Deve aplicar filtro com clientId e licensePlate nulos")
    void shouldApplyFilterWithNullClientIdAndLicensePlate() {
        GetAllWorkOrdersQuery query = new GetAllWorkOrdersQuery(null, null, 0, 10, "dateCreated", "ASC");

        WorkOrderPageResult expectedResult = new WorkOrderPageResult(List.of(mock(WorkOrder.class)), 1L);
        when(gateway.findAll(any())).thenReturn(expectedResult);

        useCase.execute(query);

        verify(gateway).findAll(pageQueryCaptor.capture());

        var capturedQuery = pageQueryCaptor.getValue();
        assertNull(capturedQuery.filter().clientId());
        assertNull(capturedQuery.filter().licensePlate());
    }

    @Test
    @DisplayName("Deve aplicar sort explícito quando apenas sortBy é nulo mas direction não")
    void shouldApplyExplicitSortWhenOnlySortByIsNull() {
        GetAllWorkOrdersQuery query = new GetAllWorkOrdersQuery(null, null, 0, 10, null, "DESC");

        WorkOrderPageResult expectedResult = new WorkOrderPageResult(List.of(mock(WorkOrder.class)), 1L);
        when(gateway.findAll(any())).thenReturn(expectedResult);

        useCase.execute(query);

        verify(gateway).findAll(pageQueryCaptor.capture());

        var capturedQuery = pageQueryCaptor.getValue();
        assertEquals(1, capturedQuery.sorts().size());
        assertEquals(null, capturedQuery.sorts().get(0).field());
        assertEquals("DESC", capturedQuery.sorts().get(0).direction());
    }

    @Test
    @DisplayName("Deve aplicar sort explícito quando apenas direction é nulo mas sortBy não")
    void shouldApplyExplicitSortWhenOnlyDirectionIsNull() {
        GetAllWorkOrdersQuery query = new GetAllWorkOrdersQuery(null, null, 0, 10, "dateCreated", null);

        WorkOrderPageResult expectedResult = new WorkOrderPageResult(List.of(mock(WorkOrder.class)), 1L);
        when(gateway.findAll(any())).thenReturn(expectedResult);

        useCase.execute(query);

        verify(gateway).findAll(pageQueryCaptor.capture());

        var capturedQuery = pageQueryCaptor.getValue();
        assertEquals(1, capturedQuery.sorts().size());
        assertEquals("dateCreated", capturedQuery.sorts().get(0).field());
        assertEquals(null, capturedQuery.sorts().get(0).direction());
    }

    private void assertFilter(WorkOrderPageQuery query, UUID clientId, String licensePlate) {
        assertEquals(clientId, query.filter().clientId());
        assertEquals(licensePlate, query.filter().licensePlate());
        assertEquals(Set.of(
                WorkOrderStatus.IN_EXECUTION,
                WorkOrderStatus.AWAITING_EXECUTION,
                WorkOrderStatus.DIAGNOSED,
                WorkOrderStatus.RECEIVED
        ), query.filter().statuses());
    }
}
