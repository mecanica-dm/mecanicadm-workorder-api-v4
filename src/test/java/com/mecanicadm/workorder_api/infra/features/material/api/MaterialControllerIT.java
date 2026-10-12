package com.mecanicadm.workorder_api.infra.features.material.api;

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import com.mecanicadm.workorder_api.core.material.usecase.command.CreateMaterialCommand;
import com.mecanicadm.workorder_api.core.material.usecase.command.UpdateMaterialCommand;
import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static com.mecanicadm.workorder_api.testutils.AuthUtils.getAuthToken;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MaterialControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StockMovementsGateway gateway;

    private String authToken;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        authToken = getAuthToken("material-admin@example.com", "password123", "Material Admin");
    }

    @Test
    @DisplayName("Deve criar um material e inicializar o estoque")
    void shouldCreateMaterialAndInitializeStock() {
        CreateMaterialCommand command = new CreateMaterialCommand(
                "Pastilha de Freio",
                "Brembo",
                "Pastilha cerâmica dianteira",
                new BigDecimal("250.00"),
                MaterialType.PART,
                10
        );

        String materialIdStr = RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .when()
                .post("/materials")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(not(emptyString()))
                .extract().asString().replace("\"", "");

        UUID materialId = UUID.fromString(materialIdStr);
        assertTrue(gateway.findByMaterialId(materialId).isPresent());
        StockMovements stockMovements = gateway.findByMaterialId(materialId).get();
        assertEquals(10, stockMovements.getQuantity());
    }

    @Test
    @DisplayName("Deve buscar material por ID")
    void shouldFindMaterialById() {
        CreateMaterialCommand createCmd = new CreateMaterialCommand("Filtro de Óleo", "Mann", "Filtro W712", new BigDecimal("50.00"), MaterialType.CONSUMABLE, 1);

        String id = RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createCmd)
                .post("/materials")
                .then()
                .extract().asString().replace("\"", "");

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/materials/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("Filtro de Óleo"))
                .body("type", equalTo("CONSUMABLE"));
    }

    @Test
    @DisplayName("Deve filtrar materiais por nome, marca e tipo")
    void shouldFilterMaterials() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        CreateMaterialCommand m1 = new CreateMaterialCommand("Pastilha de Freio " + uniqueId, "Brembo" + uniqueId, "D1", new BigDecimal("10"), MaterialType.PART, 1);
        CreateMaterialCommand m2 = new CreateMaterialCommand("Filtro de Ar " + uniqueId, "Mann" + uniqueId, "D2", new BigDecimal("20"), MaterialType.CONSUMABLE, 1);
        CreateMaterialCommand m3 = new CreateMaterialCommand("Disco de Freio " + uniqueId, "Brembo" + uniqueId, "D3", new BigDecimal("30"), MaterialType.PART, 1);

        RestAssuredMockMvc.given().header("Authorization", "Bearer " + authToken).contentType(MediaType.APPLICATION_JSON).body(m1).post("/materials");
        RestAssuredMockMvc.given().header("Authorization", "Bearer " + authToken).contentType(MediaType.APPLICATION_JSON).body(m2).post("/materials");
        RestAssuredMockMvc.given().header("Authorization", "Bearer " + authToken).contentType(MediaType.APPLICATION_JSON).body(m3).post("/materials");

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .queryParam("name", "freio " + uniqueId)
                .when()
                .get("/materials")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(2))
                .body("content.name", hasItems("Pastilha de Freio " + uniqueId, "Disco de Freio " + uniqueId));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .queryParam("brand", "Brembo" + uniqueId)
                .when()
                .get("/materials")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(2));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .queryParam("type", "CONSUMABLE")
                .when()
                .get("/materials")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(1))
                .body("content[0].name", equalTo("Filtro de Ar " + uniqueId));
    }

    @Test
    @DisplayName("Deve atualizar um material")
    void shouldUpdateMaterial() {
        CreateMaterialCommand createCmd = new CreateMaterialCommand("Nome Antigo", "Marca", "Desc", new BigDecimal("10"), MaterialType.PART, 1);
        String id = RestAssuredMockMvc.given().header("Authorization", "Bearer " + authToken).contentType(MediaType.APPLICATION_JSON).body(createCmd).post("/materials").then().extract().asString().replace("\"", "");

        UpdateMaterialCommand updateCmd = new UpdateMaterialCommand(null, "Nome Novo", "Marca Nova", "Desc Nova", new BigDecimal("20"), MaterialType.CONSUMABLE);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateCmd)
                .when()
                .put("/materials/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/materials/{id}", id)
                .then()
                .body("name", equalTo("Nome Novo"))
                .body("brand", equalTo("Marca Nova"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar material com dados invalidos")
    void shouldReturn400WhenCreateCommandIsInvalid() {
        CreateMaterialCommand invalidCommand = new CreateMaterialCommand(null, null, null, null, null, 0);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidCommand)
                .when()
                .post("/materials")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deve deletar um material")
    void shouldDeleteMaterial() {
        CreateMaterialCommand createCmd = new CreateMaterialCommand("Para Deletar", "Marca", "Desc", new BigDecimal("10"), MaterialType.PART, 1);
        String id = RestAssuredMockMvc.given().header("Authorization", "Bearer " + authToken).contentType(MediaType.APPLICATION_JSON).body(createCmd).post("/materials").then().extract().asString().replace("\"", "");

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/materials/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/materials/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
