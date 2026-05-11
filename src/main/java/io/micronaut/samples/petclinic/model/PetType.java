package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

/**
 * Entity representing a type of pet (e.g., dog, cat, bird).
 */
@MappedEntity("TYPES")
@Serdeable
public record PetType(
        @Id
        @GeneratedValue
        Integer id,

        @MappedProperty("NAME")
        @NotBlank
        String name
) implements NamedEntity {

    public PetType() {
        this(null, null);
    }

    public PetType(String name) {
        this(null, name);
    }

    public PetType withId(Integer id) {
        return new PetType(id, name);
    }

    public PetType withName(String name) {
        return new PetType(id, name);
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
