package com.mecanicadm.workorder_api.infra.validation;

import com.mecanicadm.workorder_api.shared.validation.CpfCnpjValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CpfCnpjValidatorTest {

    private CpfCnpjValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new CpfCnpjValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("Deve retornar true para valor null")
    void shouldReturnTrueForNullValue() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    @DisplayName("Deve retornar true para valor vazio ou apenas com espaços")
    void shouldReturnTrueForEmptyOrBlankValue() {
        assertTrue(validator.isValid("", context));
        assertTrue(validator.isValid("   ", context));
    }

    @Test
    @DisplayName("Deve retornar false para formato inválido (letras, tamanho incorreto, etc)")
    void shouldReturnFalseForInvalidFormat() {
        assertFalse(validator.isValid("abc", context));
        assertFalse(validator.isValid("1234567890", context));
        assertFalse(validator.isValid("123456789012", context));
        assertFalse(validator.isValid("123456789012345", context));
        assertFalse(validator.isValid("123.456.789-01", context));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com números repetidos")
    void shouldReturnFalseForRepeatedCpfDigits() {
        assertFalse(validator.isValid("00000000000", context));
        assertFalse(validator.isValid("11111111111", context));
        assertFalse(validator.isValid("99999999999", context));
    }

    @Test
    @DisplayName("Deve retornar false para CPF inválido")
    void shouldReturnFalseForInvalidCpf() {
        assertFalse(validator.isValid("12345678901", context));
    }

    @Test
    @DisplayName("Deve retornar true para CPF válido")
    void shouldReturnTrueForValidCpf() {
        assertTrue(validator.isValid("52998224725", context));
        assertTrue(validator.isValid("12345678909", context));
    }

    @Test
    @DisplayName("Deve retornar false para CNPJ com números repetidos")
    void shouldReturnFalseForRepeatedCnpjDigits() {
        assertFalse(validator.isValid("00000000000000", context));
        assertFalse(validator.isValid("11111111111111", context));
        assertFalse(validator.isValid("99999999999999", context));
    }

    @Test
    @DisplayName("Deve retornar false para CNPJ inválido")
    void shouldReturnFalseForInvalidCnpj() {
        assertFalse(validator.isValid("14195485000191", context));
    }

    @Test
    @DisplayName("Deve retornar true para CNPJ válido")
    void shouldReturnTrueForValidCnpj() {
        assertTrue(validator.isValid("11444777000161", context));
        assertTrue(validator.isValid("11222333000181", context));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com primeiro dígito verificador inválido")
    void shouldReturnFalseForCpfWithFirstCheckDigitMismatch() {
        String cpf = buildCpfWithFirstCheckDigitMismatch("529982247");
        assertFalse(validator.isValid(cpf, context));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com segundo dígito verificador inválido")
    void shouldReturnFalseForCpfWithSecondCheckDigitMismatch() {
        String cpf = buildCpfWithSecondCheckDigitMismatch("529982247");
        assertFalse(validator.isValid(cpf, context));
    }

    @Test
    @DisplayName("Deve cobrir normalizacao do segundo dígito do CPF para zero")
    void shouldCoverCpfSecondDigitNormalizationToZero() {
        String cpfWithSecondDigitZero = findValidCpfWithSecondDigitZero();
        assertTrue(validator.isValid(cpfWithSecondDigitZero, context));
    }

    @Test
    @DisplayName("Deve retornar false quando validacao interna de CPF lancar excecao")
    void shouldReturnFalseWhenCpfInternalValidationThrowsException() throws Exception {
        assertFalse(invokePrivateValidation("isValidCPF", "1234567"));
    }

    @Test
    @DisplayName("Deve retornar false para CNPJ com primeiro dígito verificador inválido")
    void shouldReturnFalseForCnpjWithFirstCheckDigitMismatch() {
        String cnpj = buildCnpjWithFirstCheckDigitMismatch("114447770001");
        assertFalse(validator.isValid(cnpj, context));
    }

    @Test
    @DisplayName("Deve retornar false para CNPJ com segundo dígito verificador inválido")
    void shouldReturnFalseForCnpjWithSecondCheckDigitMismatch() {
        String cnpj = buildCnpjWithSecondCheckDigitMismatch("114447770001");
        assertFalse(validator.isValid(cnpj, context));
    }

    @Test
    @DisplayName("Deve cobrir normalizacao do segundo dígito do CNPJ para zero")
    void shouldCoverCnpjSecondDigitNormalizationToZero() {
        String cnpjWithSecondDigitZero = findValidCnpjWithSecondDigitZero();
        assertTrue(validator.isValid(cnpjWithSecondDigitZero, context));
    }

    @Test
    @DisplayName("Deve retornar false quando validacao interna de CNPJ lancar excecao")
    void shouldReturnFalseWhenCnpjInternalValidationThrowsException() throws Exception {
        assertFalse(invokePrivateValidation("isValidCNPJ", "1234567890123"));
    }

    private boolean invokePrivateValidation(String methodName, String value) throws Exception {
        Method method = CpfCnpjValidator.class.getDeclaredMethod(methodName, String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(validator, value);
    }

    private String buildCpfWithFirstCheckDigitMismatch(String baseNineDigits) {
        int digito1 = calculateCpfFirstDigit(baseNineDigits);
        int digito2 = calculateCpfSecondDigit(baseNineDigits + digito1);
        int wrongDigit1 = (digito1 + 1) % 10;
        return baseNineDigits + wrongDigit1 + digito2;
    }

    private String buildCpfWithSecondCheckDigitMismatch(String baseNineDigits) {
        int digito1 = calculateCpfFirstDigit(baseNineDigits);
        int digito2 = calculateCpfSecondDigit(baseNineDigits + digito1);
        int wrongDigit2 = (digito2 + 1) % 10;
        return baseNineDigits + digito1 + wrongDigit2;
    }

    private int calculateCpfFirstDigit(String baseNineDigits) {
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (baseNineDigits.charAt(i) - '0') * (10 - i);
        }
        int digito = 11 - (soma % 11);
        return digito > 9 ? 0 : digito;
    }

    private int calculateCpfSecondDigit(String baseTenDigits) {
        int soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (baseTenDigits.charAt(i) - '0') * (11 - i);
        }
        int digito = 11 - (soma % 11);
        return digito > 9 ? 0 : digito;
    }

    private String findValidCpfWithSecondDigitZero() {
        for (int i = 0; i < 1_000_000; i++) {
            String base = String.format("%09d", i);
            int digito1 = calculateCpfFirstDigit(base);
            int digito2 = calculateCpfSecondDigit(base + digito1);
            String cpf = base + digito1 + digito2;

            if (digito2 == 0 && !cpf.matches("(\\d)\\1{10}")) {
                return cpf;
            }
        }
        throw new IllegalStateException("Nao foi encontrado CPF valido com segundo digito zero");
    }

    private String buildCnpjWithFirstCheckDigitMismatch(String baseTwelveDigits) {
        int digito1 = calculateCnpjFirstDigit(baseTwelveDigits);
        int digito2 = calculateCnpjSecondDigit(baseTwelveDigits + digito1);
        int wrongDigit1 = (digito1 + 1) % 10;
        return baseTwelveDigits + wrongDigit1 + digito2;
    }

    private String buildCnpjWithSecondCheckDigitMismatch(String baseTwelveDigits) {
        int digito1 = calculateCnpjFirstDigit(baseTwelveDigits);
        int digito2 = calculateCnpjSecondDigit(baseTwelveDigits + digito1);
        int wrongDigit2 = (digito2 + 1) % 10;
        return baseTwelveDigits + digito1 + wrongDigit2;
    }

    private int calculateCnpjFirstDigit(String baseTwelveDigits) {
        int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += (baseTwelveDigits.charAt(i) - '0') * peso1[i];
        }
        int digito = 11 - (soma % 11);
        return digito > 9 ? 0 : digito;
    }

    private int calculateCnpjSecondDigit(String baseThirteenDigits) {
        int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += (baseThirteenDigits.charAt(i) - '0') * peso2[i];
        }
        int digito = 11 - (soma % 11);
        return digito > 9 ? 0 : digito;
    }

    private String findValidCnpjWithSecondDigitZero() {
        for (long i = 0; i < 1_000_000L; i++) {
            String base = String.format("%012d", i);
            int digito1 = calculateCnpjFirstDigit(base);
            int digito2 = calculateCnpjSecondDigit(base + digito1);
            String cnpj = base + digito1 + digito2;

            if (digito2 == 0 && !cnpj.matches("(\\d)\\1{13}")) {
                return cnpj;
            }
        }
        throw new IllegalStateException("Nao foi encontrado CNPJ valido com segundo digito zero");
    }
}