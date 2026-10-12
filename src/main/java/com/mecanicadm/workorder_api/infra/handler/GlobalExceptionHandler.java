package com.mecanicadm.workorder_api.infra.handler;

import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.infra.security.exception.SecurityException;
import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_FIELD_NAME = "error";
    private static final String DEFAULT_VALIDATION_CODE = "validation.error.unknown";

    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<Map<String, String>> handleTechnicalException(TechnicalException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getCode(), ex.getArgs(), "Ocorreu um erro técnico inesperado.", locale);
        LOGGER.error("Erro técnico: {} - Mensagem: {}", ex.getCode(), message, ex);
        Map<String, String> response = new HashMap<>();
        response.put(ERROR_FIELD_NAME, message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UserExceptions.BadCredentials.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(UserExceptions.BadCredentials ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), ex.getMessageKey(), locale);
        Map<String, String> response = new HashMap<>();
        response.put(ERROR_FIELD_NAME, message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(DomainExceptionCore.class)
    public ResponseEntity<Map<String, String>> handleDomainException(DomainExceptionCore ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), ex.getMessageKey(), locale);
        Map<String, String> response = new HashMap<>();
        response.put(ERROR_FIELD_NAME, message);
        return ResponseEntity.status(HttpStatus.valueOf(ex.getStatus())).body(response);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex, Locale locale) {
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale);
        Map<String, String> response = new HashMap<>();
        response.put(ERROR_FIELD_NAME, message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex, Locale locale) {
        String message = messageSource.getMessage("error.bad.credentials", null, locale);
        Map<String, String> response = new HashMap<>();
        response.put(ERROR_FIELD_NAME, message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex, Locale locale) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage() != null
                    ? error.getDefaultMessage()
                    : messageSource.getMessage(DEFAULT_VALIDATION_CODE, null, DEFAULT_VALIDATION_CODE, locale);
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex, Locale locale) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String message = violation.getMessage();
            errors.put(violation.getPropertyPath().toString(),
                    message != null && message.startsWith("{")
                            ? messageSource.getMessage(message.replaceAll("[{}]", ""), null, message, locale)
                            : message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex, Locale locale) {
        LOGGER.error("Erro genérico não tratado", ex);
        String message = messageSource.getMessage("error.technical.unexpected", null, "Ocorreu um erro interno no servidor", locale);
        Map<String, String> response = new HashMap<>();
        response.put(ERROR_FIELD_NAME, message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
