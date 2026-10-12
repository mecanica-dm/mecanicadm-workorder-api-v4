package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderPageResult;
import com.mecanicadm.workorder_api.core.workorder.usecase.*;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.CreateWorkOrderRequest;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.UpdateWorkOrderRequest;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class WorkOrderControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateWorkOrderUseCase createWorkOrderUseCase;

    @MockitoBean
    private UpdateWorkOrderUseCase updateWorkOrderUseCase;

    @MockitoBean
    private GetWorkOrderByIdUseCase getWorkOrderByIdUseCase;

    @MockitoBean
    private GetAllWorkOrderUseCase getAllWorkOrderUseCase;

    @MockitoBean
    private SoftDeleteWorkOrderUseCase softDeleteWorkOrderUseCase;

    @MockitoBean
    private GetWorkOrderStatusUseCase getWorkOrderStatusUseCase;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve criar work order e retornar 201 Created")
    void shouldCreateWorkOrderAndReturn201() {
        UUID workOrderId = UUID.randomUUID();
        when(createWorkOrderUseCase.execute(any())).thenReturn(workOrderId);

        CreateWorkOrderRequest request = new CreateWorkOrderRequest(UUID.randomUUID(), "ABC-1234", "Troca de oleo", null, null);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .post("/work-orders")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(equalTo("\"" + workOrderId.toString() + "\""));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 ao criar work order com dados invalidos")
    void shouldReturn400WhenCreateWorkOrderIsInvalid() {
        CreateWorkOrderRequest invalidRequest = new CreateWorkOrderRequest(null, "", "", null, null);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidRequest)
                .when()
                .post("/work-orders")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar work order e retornar 200 OK")
    void shouldUpdateWorkOrderAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        UpdateWorkOrderRequest request = new UpdateWorkOrderRequest("DEF-5678", UUID.randomUUID(), "Descricao atualizada");

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .put("/work-orders/{id}", workOrderId)
                .then()
                .statusCode(HttpStatus.OK.value());

        verify(updateWorkOrderUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
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

        RestAssuredMockMvc.given()
                .when()
                .get("/work-orders/{id}", workOrderId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(workOrderId.toString()))
                .body("vehicleId", equalTo("ABC-1234"))
                .body("description", equalTo("Troca de oleo"))
                .body("status", equalTo("RECEIVED"));
    }

    @Test
    @WithMockUser
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

        RestAssuredMockMvc.given()
                .when()
                .get("/work-orders")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].description", equalTo("Troca de oleo"))
                .body("page.totalElements", equalTo(1));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve consultar status da work order e retornar 200 OK")
    void shouldFindStatusAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        when(getWorkOrderStatusUseCase.execute(any())).thenReturn(WorkOrderStatus.DIAGNOSED);

        RestAssuredMockMvc.given()
                .when()
                .get("/work-orders/{id}/status", workOrderId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(workOrderId.toString()))
                .body("status", equalTo("DIAGNOSED"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve deletar work order e retornar 204 No Content")
    void shouldDeleteWorkOrderAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .delete("/work-orders/{id}", workOrderId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(softDeleteWorkOrderUseCase, times(1)).execute(any());
    }
}
