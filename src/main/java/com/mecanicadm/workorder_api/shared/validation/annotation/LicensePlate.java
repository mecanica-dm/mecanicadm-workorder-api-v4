package com.mecanicadm.workorder_api.shared.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Pattern(regexp = "^([A-Za-z]{3}-?\\d{4}|[A-Za-z]{3}\\d[A-Za-z]\\d{2})$")
@ReportAsSingleViolation
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LicensePlate {
    String message() default "{validation.vehicle.licensePlate.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}