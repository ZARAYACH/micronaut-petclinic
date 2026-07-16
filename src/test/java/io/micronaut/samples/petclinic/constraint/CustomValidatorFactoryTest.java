package io.micronaut.samples.petclinic.constraint;

import io.micronaut.samples.petclinic.annotation.PasswordMatch;
import io.micronaut.samples.petclinic.annotation.ValidVisitDate;
import io.micronaut.samples.petclinic.dto.SignUpForm;
import io.micronaut.samples.petclinic.dto.VisitForm;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for application custom validation constraints.
 */
@MicronautTest
class CustomValidatorFactoryTest {

    @Inject
    Validator validator;

    @Test
    void shouldAllowFutureVisitDate() {
        Set<ConstraintViolation<VisitForm>> violations = validator.validate(
                new VisitForm(LocalDate.now().plusDays(1), "Follow-up")
        );

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectVisitDateThatIsNotInTheFuture() {
        Set<ConstraintViolation<VisitForm>> violations = validator.validate(
                new VisitForm(LocalDate.now(), "Follow-up")
        );

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("date");
                    assertThat(annotationType(violation)).isEqualTo(ValidVisitDate.class);
                });
    }

    @Test
    void shouldAllowMatchingSignupPasswords() {
        Set<ConstraintViolation<SignUpForm>> violations = validator.validate(
                new SignUpForm("new-user@example.com", "password123", "password123")
        );

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectMismatchedSignupPasswords() {
        Set<ConstraintViolation<SignUpForm>> violations = validator.validate(
                new SignUpForm("new-user@example.com", "password123", "different123")
        );

        assertThat(violations)
                .anySatisfy(violation -> assertThat(annotationType(violation)).isEqualTo(PasswordMatch.class));
    }

    private static Class<? extends Annotation> annotationType(ConstraintViolation<?> violation) {
        return violation.getConstraintDescriptor().getAnnotation().annotationType();
    }
}
