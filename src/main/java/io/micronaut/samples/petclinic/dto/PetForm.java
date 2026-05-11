package io.micronaut.samples.petclinic.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for Pet form submissions.
 */
@Introspected
@Serdeable
public record PetForm(
        Integer id,

        @NotBlank(message = "Pet name is required")
        @Size(min = 1, max = 30, message = "Pet name must be between 1 and 30 characters")
        String name,

        @NotNull(message = "Birth date is required")
        LocalDate birthDate,

        @NotNull(message = "Pet type is required")
        Integer typeId
) {

    public PetForm() {
        this(null, null, null, null);
    }
}
