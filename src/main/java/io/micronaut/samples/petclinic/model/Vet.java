package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Transient;
import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.Wither;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Entity representing a veterinarian.
 * A vet can have multiple specialties.
 */
@MappedEntity("VETS")
@Serdeable
@Wither
public record Vet(
        @Id
        @GeneratedValue
        Integer id,

        @MappedProperty("FIRST_NAME")
        @NotBlank
        String firstName,

        @MappedProperty("LAST_NAME")
        @NotBlank
        String lastName,

        @Transient
        Set<Specialty> specialties
) implements Person, VetWither {

    public Vet {
        specialties = specialties != null ? Set.copyOf(specialties) : Set.of();
    }

    public Vet() {
        this(null, null, null, Set.of());
    }

    @Creator
    public Vet(Integer id, String firstName, String lastName) {
        this(id, firstName, lastName, Set.of());
    }

    public Vet(String firstName, String lastName) {
        this(null, firstName, lastName, Set.of());
    }

    @Override
    @Transient
    public Set<Specialty> specialties() {
        return specialties;
    }

    @Transient
    public Set<Specialty> getSpecialties() {
        List<Specialty> sortedSpecialties = new ArrayList<>(specialties());
        sortedSpecialties.sort(Comparator.comparing(Specialty::getName));
        return Collections.unmodifiableSet(new LinkedHashSet<>(sortedSpecialties));
    }

    @Transient
    public int getNrOfSpecialties() {
        return specialties.size();
    }

    @Override
    public Vet withId(Integer id) {
        return new Vet(id, firstName, lastName, specialties);
    }

    @Override
    public Vet withFirstName(String firstName) {
        return new Vet(id, firstName, lastName, specialties);
    }

    @Override
    public Vet withLastName(String lastName) {
        return new Vet(id, firstName, lastName, specialties);
    }

    @Override
    public Vet withSpecialties(Set<Specialty> specialties) {
        return new Vet(id, firstName, lastName, specialties);
    }

    public Vet withSpecialtyAdded(Specialty specialty) {
        Set<Specialty> updatedSpecialties = new TreeSet<>(Comparator.comparing(Specialty::getName));
        updatedSpecialties.addAll(specialties);
        updatedSpecialties.add(specialty);
        return withSpecialties(updatedSpecialties);
    }

    public Vet withoutSpecialty(Specialty specialty) {
        Set<Specialty> updatedSpecialties = new TreeSet<>(Comparator.comparing(Specialty::getName));
        updatedSpecialties.addAll(specialties);
        updatedSpecialties.remove(specialty);
        return withSpecialties(updatedSpecialties);
    }

    @Transient
    public String getSpecialtiesAsString() {
        if (specialties.isEmpty()) {
            return "none";
        }
        List<String> names = new ArrayList<>();
        for (Specialty specialty : getSpecialties()) {
            names.add(specialty.getName());
        }
        return String.join(", ", names);
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
        return "Vet{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialties=" + getSpecialtiesAsString() +
                '}';
    }
}
