package com.example.collaborativetools.global.annotation;

import com.example.collaborativetools.global.validator.UsernameValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface Username {
    String message() default "username";

    Class[] groups() default {};

    Class[] payload() default {};
}
