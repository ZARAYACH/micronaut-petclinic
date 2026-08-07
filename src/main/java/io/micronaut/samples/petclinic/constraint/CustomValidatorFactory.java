package io.micronaut.samples.petclinic.constraint;

import io.micronaut.context.annotation.Factory;
import io.micronaut.samples.petclinic.annotation.PasswordMatch;
import io.micronaut.samples.petclinic.dto.SignUpForm;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import jakarta.inject.Singleton;

import java.time.LocalDate;

@Factory
public class CustomValidatorFactory {

    @Singleton
    ConstraintValidator<PasswordMatch, SignUpForm> passwordMatchValidator() {
        return (value, annotationMetadata, context) -> {
            if (value == null) {
                return true;
            }
            if (value.password() == null && value.repeatPassword() == null) {
                return true;
            }
            if (value.password() != null && value.repeatPassword() == null) {
                return false;
            }
            if (value.password() == null) {
                return false;
            }
            return value.password().equals(value.repeatPassword());
        };
    }

}
