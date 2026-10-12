package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderPageResult;
import com.mecanicadm.workorder_api.core.workorder.usecase.*;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.CreateWorkOrderRequest;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.UpdateWorkOrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderControllerTest {

    @Mock
    private CreateWorkOrderUseCase createWorkOrderUseCase;
    @Mock
    private UpdateWorkOrderUseCase updateWorkOrderUseCase;
    @Mock
    private GetWorkOrderByIdUseCase getWorkOrderByIdUseCase;
    @Mock
    private GetAllWorkOrderUseCase getAllWorkOrderUseCase;
    @Mock
    private SoftDeleteWorkOrderUseCase softDeleteWorkOrderUseCase;
    @Mock
    private GetWorkOrderStatusUseCase getWorkOrderStatusUseCase;

    private WorkOrderController controller;

    @BeforeEach
    void setUp() {
        controller = new WorkOrderController(
                createWorkOrderUseCase, updateWorkOrderUseCase,
                getWorkOrderByIdUseCase, getAllWorkOrderUseCase,
                softDeleteWorkOrderUseCase, getWorkOrderStatusUseCase
        );
    }

    @Test
    @DisplayName("Deve criar work order e retornar 201 Created")
    void shouldCreateWorkOrderAndReturn201() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/work-orders");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UUID workOrderId = UUID.randomUUID();
        when(createWorkOrderUseCase.execute(any())).thenReturn(workOrderId);

        CreateWorkOrderRequest createRequest = new CreateWorkOrderRequest(UUID.randomUUID(), "ABC-1234", "Troca de oleo", null, null);
        var response = controller.create(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(workOrderId, response.getBody());
        assertNotNull(response.getHeaders().getLocation());
        verify(createWorkOrderUseCase).execute(any());

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Deve atualizar work order e retornar 200 OK")
    void shouldUpdateWorkOrderAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        UpdateWorkOrderRequest request = new UpdateWorkOrderRequest("DEF-5678", UUID.randomUUID(), "Descricao atualizada");

        var response = controller.update(workOrderId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(updateWorkOrderUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve buscar work order por ID e retornar 200 OK")
    void shouldFindWorkOrderByIdAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        WorkOrder workOrder = WorkOrder.restore(
                workOrderId, clientId, "ABC-1234", "Troca de oleo",
                WorkOrderStatus.RECEIVED, null, null,
                new HashSet<>(), new HashSet<>(), null,
                LocalDateTime.now(), LocalDateTime.now(), null
        );
        when(getWorkOrderByIdUseCase.execute(any())).thenReturn(workOrder);

        var response = controller.findById(workOrderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(workOrderId, response.getBody().id());
        assertEquals("ABC-1234", response.getBody().vehicleId());
        assertEquals("Troca de oleo", response.getBody().description());
        assertEquals(WorkOrderStatus.RECEIVED, response.getBody().status());
    }

    @Test
    @DisplayName("Deve consultar status da work order e retornar 200 OK")
    void shouldFindStatusAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        when(getWorkOrderStatusUseCase.execute(any())).thenReturn(WorkOrderStatus.DIAGNOSED);

        var response = controller.findStatus(workOrderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(workOrderId, response.getBody().id());
        assertEquals(WorkOrderStatus.DIAGNOSED, response.getBody().status());
    }

    @Test
    @DisplayName("Deve listar work orders e retornar 200 OK")
    void shouldGetAllWorkOrdersAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        WorkOrder workOrder = WorkOrder.restore(
                workOrderId, clientId, "ABC-1234", "Troca de oleo",
                WorkOrderStatus.RECEIVED, null, null,
                new HashSet<>(), new HashSet<>(), null,
                LocalDateTime.now(), LocalDateTime.now(), null
        );
        var pageResult = new WorkOrderPageResult(List.of(workOrder), 1L);
        when(getAllWorkOrderUseCase.execute(any())).thenReturn(pageResult);

        Pageable pageable = PageRequest.of(0, 20, Sort.by("status").ascending());
        var response = controller.getAll(null, null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(workOrderId, response.getBody().getContent().getFirst().id());
    }

    @Test
    @DisplayName("Deve listar work orders com filtros e retornar 200 OK")
    void shouldGetAllWorkOrdersWithFiltersAndReturn200() {
        UUID clientId = UUID.randomUUID();
        var pageResult = new WorkOrderPageResult(List.of(), 0L);
        when(getAllWorkOrderUseCase.execute(any())).thenReturn(pageResult);

        Pageable pageable = PageRequest.of(0, 10);
        var response = controller.getAll(clientId, "ABC-1234", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getContent().size());
        verify(getAllWorkOrderUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve deletar work order e retornar 204 No Content")
    void shouldDeleteWorkOrderAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        var response = controller.delete(workOrderId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(softDeleteWorkOrderUseCase).execute(any());
    }
}
