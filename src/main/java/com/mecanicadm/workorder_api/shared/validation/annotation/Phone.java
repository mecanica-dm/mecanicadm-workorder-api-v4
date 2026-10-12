package com.mecanicadm.workorder_api.shared.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Pattern(regexp = "^[1-9]{2}9\\d{8}$")
@ReportAsSingleViolation
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {

    String message() default "{validation.client.phone.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}