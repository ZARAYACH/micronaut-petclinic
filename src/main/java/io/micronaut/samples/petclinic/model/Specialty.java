package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

/**
 * Entity representing a veterinarian specialty (e.g., surgery, dentistry).
 */
@MappedEntity("SPECIALTIES")
@Serdeable
public record Specialty(
        @Id
        @GeneratedValue
        Integer id,

        @MappedProperty("NAME")
        @NotBlank
        String name
) implements NamedEntity {

    public Specialty() {
        this(null, null);
    }

    public Specialty(String name) {
        this(null, name);
    }

    public Specialty withId(Integer id) {
        return new Specialty(id, name);
    }

    public Specialty withName(String name) {
        return new Specialty(id, name);
    }

    @Override
    public boolean equals(Object other) {
        return BaseEntity.entityEquals(this, other);
    }

    @Override
    public int hashCode() {
        return BaseEntity.entityHashCode(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
