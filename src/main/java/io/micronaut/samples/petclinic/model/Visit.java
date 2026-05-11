package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Transient;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.Builder;
import io.micronaut.sourcegen.annotations.Wither;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import static io.micronaut.data.annotation.Relation.Kind.MANY_TO_ONE;

/**
 * Entity representing a visit to the pet clinic.
 * A visit is associated with a pet and has a date and description.
 */
@MappedEntity("VISITS")
@Serdeable
@Builder
@Wither
public record Visit(
        @Id
        @GeneratedValue
        Integer id,

        @MappedProperty("VISIT_DATE")
        @NotNull
        LocalDate date,

        @MappedProperty("DESCRIPTION")
        @NotBlank
        String description,

        @Relation(MANY_TO_ONE)
        @MappedProperty("PET_ID")
        Pet pet
) implements BaseEntity, VisitWither {

    public Visit(Integer id, LocalDate date, String description, @Nullable Pet pet) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.pet = pet;
    }

    public Visit() {
        this(null, LocalDate.now(), null, null);
    }

    public Visit(LocalDate date, String description, Pet pet) {
        this(null, date, description, pet);
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Pet getPet() {
        return pet;
    }

    @Transient
    public Integer getPetId() {
        return pet != null ? pet.getId() : null;
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
        return "Visit{" +
                "id=" + id +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", petId=" + getPetId() +
                '}';
    }
}
