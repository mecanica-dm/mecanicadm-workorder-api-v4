package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.LaborExecutionStatus;
import com.mecanicadm.workorder_api.core.workorder.usecase.AddLaborToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.FinishLaborExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.GetWorkOrderLaborItemByIdUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.RemoveLaborItemFromWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.StartLaborExecutionUseCase;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class WorkOrderLaborControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StartLaborExecutionUseCase startLaborExecutionUseCase;

    @MockitoBean
    private FinishLaborExecutionUseCase finishLaborExecutionUseCase;

    @MockitoBean
    private GetWorkOrderLaborItemByIdUseCase getWorkOrderLaborItemByIdUseCase;

    @MockitoBean
    private AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;

    @MockitoBean
    private RemoveLaborItemFromWorkOrderUseCase removeLaborItemFromWorkOrderUseCase;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve adicionar labor a work order e retornar 204 No Content")
    void shouldAddLaborAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{workOrderId}/labors/{laborId}/add", workOrderId, laborId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(addLaborToWorkOrderUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve remover labor de work order e retornar 204 No Content")
    void shouldRemoveLaborAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{workOrderId}/labors/{laborItemId}/remove", workOrderId, laborItemId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(removeLaborItemFromWorkOrderUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve iniciar execucao de labor e retornar 204 No Content")
    void shouldStartLaborAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{workOrderId}/labors/{laborItemId}/start", workOrderId, laborItemId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(startLaborExecutionUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve finalizar execucao de labor e retornar 204 No Content")
    void shouldFinishLaborAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{workOrderId}/labors/{laborItemId}/finish", workOrderId, laborItemId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(finishLaborExecutionUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve buscar labor item por ID e retornar 200 OK")
    void shouldFindLaborItemByIdAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        UUID laborItemId = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();

        WorkOrderLaborItem laborItem = WorkOrderLaborItem.restore(
                laborItemId, workOrderId, laborId, null, null, LaborExecutionStatus.AWAITING_EXECUTION
        );
        when(getWorkOrderLaborItemByIdUseCase.execute(any())).thenReturn(laborItem);

        RestAssuredMockMvc.given()
                .when()
                .get("/work-orders/{workOrderId}/labors/{laborItemId}", workOrderId, laborItemId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(laborItemId.toString()))
                .body("laborId", equalTo(laborId.toString()))
                .body("status", equalTo("AWAITING_EXECUTION"));
    }
}
