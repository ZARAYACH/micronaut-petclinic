package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Transient;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static io.micronaut.data.annotation.Relation.Kind.MANY_TO_ONE;
import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_MANY;

/**
 * Entity representing a pet.
 * A pet belongs to an owner and has a type.
 */
@MappedEntity("PETS")
@Serdeable
public record Pet(
        @Id
        @GeneratedValue
        Integer id,

        @MappedProperty("NAME")
        @NotBlank
        String name,

        @MappedProperty("BIRTH_DATE")
        @NotNull
        LocalDate birthDate,

        @Relation(MANY_TO_ONE)
        @MappedProperty("TYPE_ID")
        @NotNull
        PetType type,

        @Relation(MANY_TO_ONE)
        @MappedProperty("OWNER_ID")
        Owner owner,

        @Relation(value = ONE_TO_MANY, mappedBy = "pet")
        List<Visit> visits
) implements NamedEntity {

    public Pet(Integer id,
               String name,
               LocalDate birthDate,
               PetType type,
               @Nullable Owner owner,
               List<Visit> visits) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.type = type;
        this.owner = owner;
        this.visits = visits != null ? List.copyOf(visits) : List.of();
    }

    public Pet() {
        this(null, null, null, null, null, List.of());
    }

    public Pet(String name, LocalDate birthDate, PetType type, Owner owner) {
        this(null, name, birthDate, type, owner, List.of());
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public PetType getType() {
        return type;
    }

    @Transient
    public Integer getTypeId() {
        return type != null ? type.getId() : null;
    }

    public Owner getOwner() {
        return owner;
    }

    @Transient
    public Integer getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    public List<Visit> getVisits() {
        List<Visit> sortedVisits = new ArrayList<>(visits);
        sortedVisits.sort(Comparator.comparing(Visit::getDate));
        return Collections.unmodifiableList(sortedVisits);
    }

    public Pet withId(Integer id) {
        return new Pet(id, name, birthDate, type, owner, visits);
    }

    public Pet withName(String name) {
        return new Pet(id, name, birthDate, type, owner, visits);
    }

    public Pet withBirthDate(LocalDate birthDate) {
        return new Pet(id, name, birthDate, type, owner, visits);
    }

    public Pet withType(PetType type) {
        return new Pet(id, name, birthDate, type, owner, visits);
    }

    public Pet withOwner(Owner owner) {
        return new Pet(id, name, birthDate, type, owner, visits);
    }

    public Pet withVisits(Collection<Visit> visits) {
        return new Pet(id, name, birthDate, type, owner, List.copyOf(visits));
    }

    public Pet withVisitAdded(Visit visit) {
        List<Visit> updatedVisits = new ArrayList<>(visits);
        updatedVisits.add(visit.withPet(this));
        return withVisits(updatedVisits);
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
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", type=" + (type != null ? type.getName() : null) +
                '}';
    }
}
