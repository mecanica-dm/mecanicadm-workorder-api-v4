package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import com.mecanicadm.workorder_api.core.workorder.usecase.CalculateWorkOrderBudgetUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.DecideWorkOrderBudgetUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.GetPrintableBudgetUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.ManuallyAdjustWorkOrderBudgetUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.SendWorkOrderBudgetUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableBudgetResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.DecideWorkOrderBudgetRequest;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.request.ManuallyAdjustWorkOrderBudgetRequest;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class WorkOrderBudgetControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SendWorkOrderBudgetUseCase sendWorkOrderBudgetUseCase;

    @MockitoBean
    private ManuallyAdjustWorkOrderBudgetUseCase manuallyAdjustWorkOrderBudgetUseCase;

    @MockitoBean
    private DecideWorkOrderBudgetUseCase decideWorkOrderBudgetUseCase;

    @MockitoBean
    private CalculateWorkOrderBudgetUseCase calculateWorkOrderBudgetUseCase;

    @MockitoBean
    private GetPrintableBudgetUseCase getPrintableBudgetUseCase;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve enviar orcamento e retornar 204 No Content")
    void shouldSendBudgetAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{workOrderId}/budget/send", workOrderId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(sendWorkOrderBudgetUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve ajustar orcamento manualmente e retornar 204 No Content")
    void shouldAdjustBudgetAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        ManuallyAdjustWorkOrderBudgetRequest request = new ManuallyAdjustWorkOrderBudgetRequest(BigDecimal.valueOf(500));

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .patch("/work-orders/{workOrderId}/budget", workOrderId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(manuallyAdjustWorkOrderBudgetUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 ao ajustar orcamento com preco nulo")
    void shouldReturn400WhenAdjustBudgetWithNullPrice() {
        UUID workOrderId = UUID.randomUUID();
        ManuallyAdjustWorkOrderBudgetRequest invalidRequest = new ManuallyAdjustWorkOrderBudgetRequest(null);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidRequest)
                .when()
                .patch("/work-orders/{workOrderId}/budget", workOrderId)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve recalcular orcamento e retornar 204 No Content")
    void shouldRecalculateBudgetAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{workOrderId}/budget/recalculate", workOrderId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(calculateWorkOrderBudgetUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve decidir orcamento e retornar 204 No Content")
    void shouldDecideBudgetAndReturn204() {
        UUID workOrderId = UUID.randomUUID();
        DecideWorkOrderBudgetRequest request = new DecideWorkOrderBudgetRequest(WorkOrderBudgetStatus.APPROVED, null);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .post("/work-orders/{workOrderId}/budget/decision", workOrderId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(decideWorkOrderBudgetUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 ao decidir orcamento com decisao nula")
    void shouldReturn400WhenDecideBudgetWithNullDecision() {
        UUID workOrderId = UUID.randomUUID();
        DecideWorkOrderBudgetRequest invalidRequest = new DecideWorkOrderBudgetRequest(null, null);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidRequest)
                .when()
                .post("/work-orders/{workOrderId}/budget/decision", workOrderId)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve imprimir orcamento e retornar 200 OK")
    void shouldPrintBudgetAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        PrintableBudgetResponse response = new PrintableBudgetResponse("budget.pdf", "base64content");
        when(getPrintableBudgetUseCase.execute(any())).thenReturn(response);

        RestAssuredMockMvc.given()
                .when()
                .get("/work-orders/{workOrderId}/budget/print", workOrderId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("fileName", equalTo("budget.pdf"))
                .body("base64Content", equalTo("base64content"));
    }
}
