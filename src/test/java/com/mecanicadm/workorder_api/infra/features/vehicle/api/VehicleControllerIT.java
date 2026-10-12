package com.mecanicadm.workorder_api.infra.features.vehicle.api;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehiclePageResult;
import com.mecanicadm.workorder_api.core.vehicle.usecase.*;
import com.mecanicadm.workorder_api.core.vehicle.usecase.command.DeleteVehicleCommand;
import com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.request.CreateVehicleRequest;
import com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.request.UpdateVehicleRequest;
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

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateVehicleUseCase createVehicleUseCase;

    @MockitoBean
    private GetVehicleByIdUseCase getVehicleByIdUseCase;

    @MockitoBean
    private UpdateVehicleUseCase updateVehicleUseCase;

    @MockitoBean
    private DeleteVehicleUseCase deleteVehicleUseCase;

    @MockitoBean
    private GetAllVehicleUseCase getAllVehicleUseCase;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve criar um veículo e retornar 201 Created")
    void shouldCreateVehicleAndReturn201() {
        CreateVehicleRequest request = new CreateVehicleRequest("Civic", "ABC1234", "Honda", (short) 2023);
        when(createVehicleUseCase.execute(any())).thenReturn("ABC1234");

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .post("/vehicle")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", equalTo("/vehicle/ABC1234"))
                .body(equalTo("ABC1234"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 Bad Request ao tentar criar veículo com dados inválidos")
    void shouldReturn400WhenCommandIsInvalid() {
        CreateVehicleRequest invalidCommand = new CreateVehicleRequest("", "", "", null);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidCommand)
                .when()
                .post("/vehicle")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar um veículo e retornar 204 No Content")
    void shouldUpdateVehicleAndReturn204() {
        String licensePlate = "ABC1234";
        UpdateVehicleRequest command = new UpdateVehicleRequest("Civic Updated", "Honda", (short) 2019);
        var vehicle = Vehicle.restore("Civic Updated", licensePlate, "Honda", (short) 2019, null, null, null);
        when(updateVehicleUseCase.execute(any())).thenReturn(vehicle);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .when()
                .put("/vehicle/{licensePlate}", licensePlate)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(updateVehicleUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 Bad Request ao tentar atualizar veículo com dados inválidos")
    void shouldReturn400WhenUpdateCommandIsInvalid() {
        String licensePlate = "ABC1234";
        UpdateVehicleRequest invalidCommand = new UpdateVehicleRequest("", "", null);

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidCommand)
                .when()
                .put("/vehicle/{licensePlate}", licensePlate)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve excluir um veículo e retornar 204 No Content")
    void shouldDeleteVehicleAndReturn204() {
        String licensePlate = "ABC1234";

        RestAssuredMockMvc.given()
                .postProcessors(csrf())
                .when()
                .delete("/vehicle/{licensePlate}", licensePlate)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(deleteVehicleUseCase, times(1)).execute(any(DeleteVehicleCommand.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve buscar um veículo e retornar 200 OK")
    void shouldFindVehicleAndReturn200() {
        String licensePlate = "ABC1234";
        var vehicleResponse = Vehicle.restore("Civic", licensePlate, "Honda", (short) 2023, null, null, null);
        when(getVehicleByIdUseCase.execute(any())).thenReturn(vehicleResponse);

        RestAssuredMockMvc.given()
                .when()
                .get("/vehicle/{licensePlate}", licensePlate)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("licensePlate", equalTo(licensePlate))
                .body("model", equalTo("Civic"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve listar os veículos e retornar 200 OK")
    void shouldGetAllVehiclesAndReturn200() {
        var vehicle = Vehicle.restore("Civic", "ABC1234", "Honda", (short) 2023, null, null, null);
        var pageResult = new VehiclePageResult(List.of(vehicle), 1L);
        when(getAllVehicleUseCase.execute(any())).thenReturn(pageResult);

        RestAssuredMockMvc.given()
                .when()
                .get("/vehicle")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].licensePlate", equalTo("ABC1234"))
                .body("page.totalElements", equalTo(1));

        verify(getAllVehicleUseCase, times(1)).execute(any());
    }
}
