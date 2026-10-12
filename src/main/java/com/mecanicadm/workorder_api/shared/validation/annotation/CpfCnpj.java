package com.mecanicadm.workorder_api.shared.validation.annotation;

import com.mecanicadm.workorder_api.shared.validation.CpfCnpjValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CpfCnpjValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfCnpj {

    String message() default "{validation.client.document.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}