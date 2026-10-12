package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.usecase.AddMaterialToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.RemoveMaterialItemFromWorkOrderUseCase;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.AddMaterialToWorkOrderRequest;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class WorkOrderMaterialControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;

    @MockitoBean
    private RemoveMaterialItemFromWorkOrderUseCase removeMaterialItemFromWorkOrderUseCase;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve adicionar material a work order e retornar 204 No Content")
    void shouldAddMaterialAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        AddMaterialToWorkOrderRequest request = new AddMaterialToWorkOrderRequest(2);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .post("/work-orders/{workOrderId}/materials/{materialId}/add", workOrderId, materialId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(addMaterialToWorkOrderUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 ao adicionar material com quantidade invalida")
    void shouldReturn400WhenAddMaterialWithInvalidQuantity() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        AddMaterialToWorkOrderRequest invalidRequest = new AddMaterialToWorkOrderRequest(0);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidRequest)
                .when()
                .post("/work-orders/{workOrderId}/materials/{materialId}/add", workOrderId, materialId)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve remover material de work order e retornar 204 No Content")
    void shouldRemoveMaterialAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{workOrderId}/materials/{materialId}/remove", workOrderId, materialId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(removeMaterialItemFromWorkOrderUseCase, times(1)).execute(any());
    }
}
