package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.labor.usecase.GetAllLaborExecutionTimeReportUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.GetAllWorkOrderExecutionTimeReportUseCase;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborExecutionReportResponse;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborTypeExecutionStatsResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderExecutionReportResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderExecutionTimeInfoResponse;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class WorkOrderAnalyticsControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllWorkOrderExecutionTimeReportUseCase getAllWorkOrderExecutionTimeReportUseCase;

    @MockitoBean
    private GetAllLaborExecutionTimeReportUseCase getAllLaborExecutionTimeReportUseCase;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar relatorio de tempo de execucao de work orders")
    void shouldGetWorkOrderExecutionReport() {
        var response = new WorkOrderExecutionReportResponse(
                "MINUTES", 5L, 120.0,
                new WorkOrderExecutionTimeInfoResponse(UUID.randomUUID(), 300.0),
                new WorkOrderExecutionTimeInfoResponse(UUID.randomUUID(), 30.0),
                60.0
        );
        when(getAllWorkOrderExecutionTimeReportUseCase.execute(any())).thenReturn(response);

        RestAssuredMockMvc.given()
                .when()
                .get("/analytics/work-orders/execution-time")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("durationUnit", equalTo("MINUTES"))
                .body("totalProcessedWorkOrders", equalTo(5))
                .body("averageWorkOrderExecutionTime", equalTo(120.0F));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar relatorio de tempo de execucao de labors")
    void shouldGetLaborExecutionReport() {
        var laborStats = new LaborTypeExecutionStatsResponse(UUID.randomUUID(), "Troca de Oleo", 10L, 45.0, 30.0, 60.0);
        var response = new LaborExecutionReportResponse(
                "MINUTES", 10L, 45.0, List.of(laborStats)
        );
        when(getAllLaborExecutionTimeReportUseCase.execute(any())).thenReturn(response);

        RestAssuredMockMvc.given()
                .when()
                .get("/analytics/labors/execution-time")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("durationUnit", equalTo("MINUTES"))
                .body("totalProcessedLabors", equalTo(10))
                .body("averageLaborExecutionTime", equalTo(45.0F))
                .body("statsByLaborType[0].laborName", equalTo("Troca de Oleo"));
    }
}
