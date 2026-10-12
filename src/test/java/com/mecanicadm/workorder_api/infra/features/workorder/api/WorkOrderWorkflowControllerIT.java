package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.usecase.DeliverWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.DiagnoseWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.FinishWorkOrderExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.RecordWorkOrderPaymentUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.StartWorkOrderExecutionUseCase;
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
class WorkOrderWorkflowControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiagnoseWorkOrderUseCase diagnoseWorkOrderUseCase;

    @MockitoBean
    private StartWorkOrderExecutionUseCase startWorkOrderExecutionUseCase;

    @MockitoBean
    private FinishWorkOrderExecutionUseCase finishWorkOrderExecutionUseCase;

    @MockitoBean
    private RecordWorkOrderPaymentUseCase recordWorkOrderPaymentUseCase;

    @MockitoBean
    private DeliverWorkOrderUseCase deliverWorkOrderUseCase;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve diagnosticar work order e retornar 200 OK")
    void shouldDiagnoseAndReturn200() {
        UUID workOrderId = UUID.randomUUID();
        when(diagnoseWorkOrderUseCase.execute(any())).thenReturn(workOrderId);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{id}/step/diagnose", workOrderId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("\"" + workOrderId.toString() + "\""));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve iniciar execucao e retornar 204 No Content")
    void shouldStartExecutionAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{id}/step/start-execution", workOrderId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(startWorkOrderExecutionUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve finalizar execucao e retornar 204 No Content")
    void shouldFinishExecutionAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{id}/step/finish-execution", workOrderId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(finishWorkOrderExecutionUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve registrar pagamento e retornar 204 No Content")
    void shouldRecordPaymentAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{id}/step/payment", workOrderId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(recordWorkOrderPaymentUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve entregar work order e retornar 204 No Content")
    void shouldDeliverAndReturn204() {
        UUID workOrderId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .post("/work-orders/{id}/step/deliver", workOrderId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(deliverWorkOrderUseCase, times(1)).execute(any());
    }
}
