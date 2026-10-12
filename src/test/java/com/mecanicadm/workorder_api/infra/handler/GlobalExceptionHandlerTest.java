package com.mecanicadm.workorder_api.infra.handler;

import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.infra.security.exception.SecurityException;
import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    @DisplayName("Deve retornar 401 ao capturar SecurityException")
    void shouldReturn401WhenSecurityExceptionOccurs() throws Exception {
        when(messageSource.getMessage(eq("token.invalid"), any(), eq("token.invalid"), any(Locale.class)))
                .thenReturn("Token inválido");

        mockMvc.perform(get("/test/security-exception"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token inválido"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao capturar BadCredentialsException")
    void shouldReturn401WhenBadCredentialsExceptionOccurs() throws Exception {
        when(messageSource.getMessage(eq("error.bad.credentials"), any(), any(Locale.class)))
                .thenReturn("E-mail ou senha inválidos");

        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("E-mail ou senha inválidos"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao capturar ConstraintViolationException")
    void shouldReturn400WhenConstraintViolationExceptionOccurs() throws Exception {
        mockMvc.perform(get("/test/constraint-violation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("O nome não pode ser vazio."))
                .andExpect(jsonPath("$.email").value("O e-mail deve ser válido."));
    }

    @Test
    @DisplayName("Deve retornar 500 ao capturar uma Exception genérica")
    void shouldReturn500WhenGenericExceptionOccurs() throws Exception {
        when(messageSource.getMessage(eq("error.technical.unexpected"), isNull(), eq("Ocorreu um erro interno no servidor"), any(Locale.class)))
                .thenReturn("Ocorreu um erro interno no servidor");

        mockMvc.perform(get("/test/generic-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Ocorreu um erro interno no servidor"));
    }

    @Test
    @DisplayName("Deve retornar 500 ao capturar TechnicalException")
    void shouldReturn500WhenTechnicalExceptionOccurs() throws Exception {
        when(messageSource.getMessage(eq("error.technical.entity.null"), any(), eq("Ocorreu um erro técnico inesperado."), any(Locale.class)))
                .thenReturn("Entidade nula para criação");

        mockMvc.perform(get("/test/technical-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Entidade nula para criação"));
    }

    @Test
    @DisplayName("Deve retornar status do DomainExceptionCore")
    void shouldReturnDomainExceptionStatus() throws Exception {
        when(messageSource.getMessage(eq("vehicle.not.found"), any(), eq("vehicle.not.found"), any(Locale.class)))
                .thenReturn("Veículo não encontrado");

        mockMvc.perform(get("/test/domain-exception"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Veículo não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao capturar UserExceptions.BadCredentials")
    void shouldReturn401WhenUserBadCredentialsOccurs() throws Exception {
        when(messageSource.getMessage(eq("error.bad.credentials"), any(), eq("error.bad.credentials"), any(Locale.class)))
                .thenReturn("Credenciais inválidas");

        mockMvc.perform(get("/test/user-bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciais inválidas"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao capturar MethodArgumentNotValidException")
    void shouldReturn400WhenMethodArgumentNotValidExceptionOccurs() throws Exception {
        mockMvc.perform(post("/test/validation-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @RestController
    static class TestController {
        @GetMapping("/test/security-exception")
        public void throwSecurityException() {
            throw new SecurityException("token.invalid") {};
        }

        @GetMapping("/test/bad-credentials")
        public void throwBadCredentials() {
            throw new BadCredentialsException("Bad credentials");
        }

        @GetMapping("/test/generic-exception")
        public void throwGenericException() {
            throw new RuntimeException("Unexpected error");
        }

        @GetMapping("/test/constraint-violation")
        public void throwConstraintViolation() {
            var nameViolation = mock(ConstraintViolation.class);
            var namePath = mock(Path.class);
            when(namePath.toString()).thenReturn("name");
            when(nameViolation.getPropertyPath()).thenReturn(namePath);
            when(nameViolation.getMessage()).thenReturn("O nome não pode ser vazio.");

            var emailViolation = mock(ConstraintViolation.class);
            var emailPath = mock(Path.class);
            when(emailPath.toString()).thenReturn("email");
            when(emailViolation.getPropertyPath()).thenReturn(emailPath);
            when(emailViolation.getMessage()).thenReturn("O e-mail deve ser válido.");

            throw new ConstraintViolationException("Validation failed", java.util.Set.<ConstraintViolation<?>>of(nameViolation, emailViolation));
        }

        @GetMapping("/test/technical-exception")
        public void throwTechnicalException() {
            throw new TechnicalException("error.technical.entity.null", "Entity", "criação");
        }

        @GetMapping("/test/domain-exception")
        public void throwDomainException() {
            throw new DomainExceptionCore("vehicle.not.found", 404) {};
        }

        @GetMapping("/test/user-bad-credentials")
        public void throwUserBadCredentials() {
            throw new UserExceptions.BadCredentials();
        }

        @PostMapping("/test/validation-error")
        public void throwValidationError(@Valid @RequestBody ValidatedRequest request) {
            // method body is intentionally empty - validation error occurs before execution
        }
    }

    static class ValidatedRequest {
        @NotBlank
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}