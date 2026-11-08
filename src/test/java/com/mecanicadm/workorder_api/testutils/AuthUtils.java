package com.mecanicadm.workorder_api.testutils;

import com.mecanicadm.workorder_api.core.user.usecase.command.CreateUserCommand;
import com.mecanicadm.workorder_api.core.user.usecase.query.AuthenticateUserQuery;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class AuthUtils {

    public static String getAuthToken(String email, String password, String name) {
        CreateUserCommand createCommand = new CreateUserCommand(email, password, name);
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createCommand)
                .when()
                .post("/user")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        AuthenticateUserQuery query = new AuthenticateUserQuery(email, password);
        return RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(query)
                .when()
                .post("/user/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().path("access_token");
    }

    public static String getAuthToken(String email, String name) {
        CreateUserCommand createCommand = new CreateUserCommand(email, "password123", name);
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createCommand)
                .when()
                .post("/user")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        AuthenticateUserQuery query = new AuthenticateUserQuery(email, "password123");
        return RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(query)
                .when()
                .post("/user/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().path("access_token");
    }
}
