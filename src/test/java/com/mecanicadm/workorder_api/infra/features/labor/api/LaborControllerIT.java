package com.mecanicadm.workorder_api.infra.features.labor.api;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageResult;
import com.mecanicadm.workorder_api.core.labor.usecase.CreateLaborUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.DeleteLaborUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.GetAllLaborsUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.GetLaborByIdUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.UpdateLaborUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.command.DeleteLaborCommand;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.request.CreateLaborRequest;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.request.UpdateLaborRequest;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class LaborControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateLaborUseCase createLaborUseCase;

    @MockitoBean
    private UpdateLaborUseCase updateLaborUseCase;

    @MockitoBean
    private DeleteLaborUseCase deleteLaborUseCase;

    @MockitoBean
    private GetLaborByIdUseCase getLaborByIdUseCase;

    @MockitoBean
    private GetAllLaborsUseCase getAllLaborsUseCase;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve criar labor e retornar 201 Created")
    void shouldCreateLaborAndReturn201() {
        UUID laborId = UUID.randomUUID();
        when(createLaborUseCase.execute(any())).thenReturn(laborId);

        CreateLaborRequest request = new CreateLaborRequest("Troca de Óleo", BigDecimal.valueOf(150));

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .post("/labor")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(equalTo("\"" + laborId.toString() + "\""));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 ao criar labor com dados invalidos")
    void shouldReturn400WhenCreateCommandIsInvalid() {
        CreateLaborRequest invalidRequest = new CreateLaborRequest("", null);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidRequest)
                .when()
                .post("/labor")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar labor e retornar 204 No Content")
    void shouldUpdateLaborAndReturn204() {
        UUID laborId = UUID.randomUUID();
        UpdateLaborRequest request = new UpdateLaborRequest("Troca de Óleo Atualizada", BigDecimal.valueOf(200));

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .put("/labor/{id}", laborId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(updateLaborUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve deletar labor e retornar 204 No Content")
    void shouldDeleteLaborAndReturn204() {
        UUID laborId = UUID.randomUUID();

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .delete("/labor/{id}", laborId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(deleteLaborUseCase, times(1)).execute(any(DeleteLaborCommand.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve buscar labor por ID e retornar 200 OK")
    void shouldFindLaborByIdAndReturn200() {
        UUID laborId = UUID.randomUUID();
        Labor labor = mock(Labor.class);
        when(labor.getId()).thenReturn(laborId);
        when(labor.getName()).thenReturn("Troca de Óleo");
        when(labor.getPrice()).thenReturn(BigDecimal.valueOf(150));
        when(getLaborByIdUseCase.execute(any())).thenReturn(labor);

        RestAssuredMockMvc.given()
                .when()
                .get("/labor/{id}", laborId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("Troca de Óleo"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve listar labors e retornar 200 OK")
    void shouldGetAllLaborsAndReturn200() {
        Labor labor = mock(Labor.class);
        when(labor.getId()).thenReturn(UUID.randomUUID());
        when(labor.getName()).thenReturn("Troca de Óleo");
        when(labor.getPrice()).thenReturn(BigDecimal.valueOf(150));

        var pageResult = new LaborPageResult(List.of(labor), 1L);
        when(getAllLaborsUseCase.execute(any())).thenReturn(pageResult);

        RestAssuredMockMvc.given()
                .when()
                .get("/labor")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].name", equalTo("Troca de Óleo"))
                .body("page.totalElements", equalTo(1));
    }
}
