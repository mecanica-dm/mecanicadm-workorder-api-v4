package com.mecanicadm.workorder_api.infra.features.user.api;

import com.mecanicadm.workorder_api.infra.features.user.api.dto.CreateUserRequest;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import com.mecanicadm.workorder_api.infra.features.user.api.dto.ForgotPasswordRequest;
import com.mecanicadm.workorder_api.infra.features.user.api.dto.LoginRequest;
import com.mecanicadm.workorder_api.infra.features.user.api.dto.ResetPasswordRequest;
import com.mecanicadm.workorder_api.infra.features.user.api.dto.UpdateUserRequest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.mecanicadm.workorder_api.testutils.AuthUtils.getAuthToken;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @DisplayName("Deve criar um novo usuário no banco H2")
    void shouldCreateUser() {
        CreateUserRequest request = new CreateUserRequest("brand-new-user@email.com", "password123", "New Integration User");

        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", not(emptyString()));
    }

    @Test
    @Sql(scripts = "/sql/user.sql")
    @DisplayName("Deve buscar um usuário existente por ID estando autenticado")
    void shouldFindUserById() {
        String userId = "550e8400-e29b-41d4-a716-446655440002";
        String token = getAuthToken("test-find@example.com", "Test Find User");

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/user/{id}", userId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("Forgot Password User"))
                .body("email", equalTo("forgot@example.com"));
    }

    @Test
    @Sql(scripts = "/sql/user.sql")
    @DisplayName("Deve atualizar o usuário autenticado")
    void shouldUpdateUser() {
        String token = getAuthToken("test-update@example.com", "Test Update User");
        UpdateUserRequest updateRequest = new UpdateUserRequest("Updated Name", null, null);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .when()
                .put("/user")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deve retornar 401 ao buscar usuário sem autenticação")
    void shouldReturn401WhenNotAuthenticated() {
        RestAssuredMockMvc.given()
                .when()
                .get("/user/{id}", UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Deve autenticar o usuário com sucesso")
    void shouldAuthenticateUserSuccessfully() {
        CreateUserRequest createRequest = new CreateUserRequest("login-test@example.com", "password123", "Login User");
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .post("/user");

        LoginRequest loginRequest = new LoginRequest("login-test@example.com", "password123");

        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginRequest)
                .when()
                .post("/user/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("access_token", not(emptyString()))
                .body("name", equalTo("Login User"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar logar com senha incorreta")
    void shouldReturnErrorWithWrongPassword() {
        CreateUserRequest createRequest = new CreateUserRequest("wrong-pass@example.com", "password123", "Wrong Pass User");
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .post("/user");

        LoginRequest loginRequest = new LoginRequest("wrong-pass@example.com", "wrong-password");

        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginRequest)
                .when()
                .post("/user/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("error", equalTo("E-mail ou senha inválidos."));
    }

    @Test
    @Sql(scripts = "/sql/user.sql")
    @DisplayName("Deve solicitar redefinição de senha com sucesso")
    void shouldRequestPasswordResetSuccessfully() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("forgot@example.com");

        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .post("/user/forgot-password")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @Sql(scripts = {"/sql/user.sql", "/sql/password_reset_token.sql"})
    @DisplayName("Deve redefinir a senha com um token válido")
    void shouldResetPasswordWithValidToken() {
        ResetPasswordRequest request = new ResetPasswordRequest("valid-reset-token", "new-strong-password");

        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .post("/user/reset-password")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @Sql(scripts = {"/sql/user.sql", "/sql/expired_password_reset_token.sql"})
    @DisplayName("Deve retornar 400 ao tentar redefinir senha com token expirado")
    void shouldReturnErrorWithExpiredToken() {
        ResetPasswordRequest request = new ResetPasswordRequest("expired-reset-token", "new-strong-password");

        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .post("/user/reset-password")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar usuario com dados invalidos")
    void shouldReturn400WhenCreateUserWithInvalidData() {
        CreateUserRequest invalidRequest = new CreateUserRequest("", "", "");

        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidRequest)
                .when()
                .post("/user")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deve retornar 400 ao fazer login com dados invalidos")
    void shouldReturn400WhenLoginWithInvalidData() {
        LoginRequest invalidRequest = new LoginRequest("", "");

        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidRequest)
                .when()
                .post("/user/login")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Sql(scripts = "/sql/user.sql")
    @DisplayName("Deve realizar exclusão lógica (soft delete) do usuário autenticado")
    void shouldSoftDeleteUser() {
        String token = getAuthToken("test-delete@example.com", "Test Delete User");

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/user")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
