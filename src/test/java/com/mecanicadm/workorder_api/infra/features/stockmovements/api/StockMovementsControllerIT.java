package com.mecanicadm.workorder_api.infra.features.stockmovements.api;

import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.mecanicadm.workorder_api.testutils.AuthUtils.getAuthToken;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StockMovementsControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String authToken;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        authToken = getAuthToken("admin@example.com", "password123", "Admin");
    }

    @Test
    @DisplayName("Deve buscar o extrato de estoque com sucesso")
    @Sql(scripts = "/sql/stock_movements_data.sql")
    void shouldGetStockStatementSuccessfully() {
        UUID materialId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/stock-movements/{materialId}/statement", materialId)
                .then()
                .statusCode(200)
                .body("materialId", equalTo(materialId.toString()))
                .body("currentBalance", equalTo(7))
                .body("movements", hasSize(2));
    }
}
