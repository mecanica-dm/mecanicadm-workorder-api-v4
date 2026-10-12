package com.mecanicadm.workorder_api.shared.validation;

import com.mecanicadm.workorder_api.shared.validation.annotation.CpfCnpj;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfCnpjValidator implements ConstraintValidator<CpfCnpj, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        if (!value.matches("^\\d{11}$|^\\d{14}$")) {
            return false;
        }

        if (value.length() == 11) {
            return isValidCPF(value);
        } else {
            return isValidCNPJ(value);
        }
    }

    private boolean isValidCPF(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - '0') * (10 - i);
            int digito1 = 11 - (soma % 11);
            if (digito1 > 9) digito1 = 0;

            soma = 0;
            for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - '0') * (11 - i);
            int digito2 = 11 - (soma % 11);
            if (digito2 > 9) digito2 = 0;

            return (cpf.charAt(9) - '0') == digito1 && (cpf.charAt(10) - '0') == digito2;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidCNPJ(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) return false;

        try {
            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 12; i++) soma += (cnpj.charAt(i) - '0') * peso1[i];
            int digito1 = 11 - (soma % 11);
            if (digito1 > 9) digito1 = 0;

            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            soma = 0;
            for (int i = 0; i < 13; i++) soma += (cnpj.charAt(i) - '0') * peso2[i];
            int digito2 = 11 - (soma % 11);
            if (digito2 > 9) digito2 = 0;

            return (cnpj.charAt(12) - '0') == digito1 && (cnpj.charAt(13) - '0') == digito2;
        } catch (Exception e) {
            return false;
        }
    }
}
