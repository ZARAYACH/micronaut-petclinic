package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Transient;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_MANY;

/**
 * Entity representing a pet owner.
 * An owner can have multiple pets.
 */
@MappedEntity("OWNERS")
@Serdeable
public record Owner(
        @Id
        @GeneratedValue
        Integer id,

        @MappedProperty("FIRST_NAME")
        @NotBlank
        String firstName,

        @MappedProperty("LAST_NAME")
        @NotBlank
        String lastName,

        @MappedProperty("ADDRESS")
        @NotBlank
        String address,

        @MappedProperty("CITY")
        @NotBlank
        String city,

        @MappedProperty("TELEPHONE")
        @NotBlank
        @Digits(fraction = 0, integer = 10)
        String telephone,

        @Relation(value = ONE_TO_MANY, mappedBy = "owner", cascade = Relation.Cascade.ALL)
        List<Pet> pets
) implements Person {

    public Owner {
        pets = pets != null ? List.copyOf(pets) : List.of();
    }

    public Owner() {
        this(null, null, null, null, null, null, List.of());
    }

    public Owner(String firstName, String lastName, String address, String city, String telephone) {
        this(null, firstName, lastName, address, city, telephone, List.of());
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getTelephone() {
        return telephone;
    }

    public List<Pet> getPets() {
        return pets;
    }

    @Transient
    public List<Pet> getPetsSorted() {
        List<Pet> sortedPets = new ArrayList<>(pets);
        sortedPets.sort(Comparator.comparing(Pet::getName));
        return Collections.unmodifiableList(sortedPets);
    }

    public Owner withId(Integer id) {
        return new Owner(id, firstName, lastName, address, city, telephone, pets);
    }

    public Owner withFirstName(String firstName) {
        return new Owner(id, firstName, lastName, address, city, telephone, pets);
    }

    public Owner withLastName(String lastName) {
        return new Owner(id, firstName, lastName, address, city, telephone, pets);
    }

    public Owner withAddress(String address) {
        return new Owner(id, firstName, lastName, address, city, telephone, pets);
    }

    public Owner withCity(String city) {
        return new Owner(id, firstName, lastName, address, city, telephone, pets);
    }

    public Owner withTelephone(String telephone) {
        return new Owner(id, firstName, lastName, address, city, telephone, pets);
    }

    public Owner withPets(Collection<Pet> pets) {
        return new Owner(id, firstName, lastName, address, city, telephone, List.copyOf(pets));
    }

    public Owner withPetAdded(Pet pet) {
        List<Pet> updatedPets = new ArrayList<>(pets);
        Pet ownerAwarePet = pet.withOwner(this);
        if (ownerAwarePet.isNew()) {
            updatedPets.add(ownerAwarePet);
        }
        return withPets(updatedPets);
    }

    public Owner withoutPet(Pet pet) {
        List<Pet> updatedPets = new ArrayList<>(pets);
        updatedPets.remove(pet);
        return withPets(updatedPets);
    }

    public Pet getPet(String name) {
        return getPet(name, false);
    }

    public Pet getPet(String name, boolean ignoreNew) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String lowerCaseName = name.toLowerCase();
        for (Pet pet : pets) {
            if (!ignoreNew || !pet.isNew()) {
                if (pet.getName().toLowerCase().equals(lowerCaseName)) {
                    return pet;
                }
            }
        }
        return null;
    }

    public Pet getPet(Integer id) {
        for (Pet pet : pets) {
            if (pet.getId().equals(id)) {
                return pet;
            }
        }
        return null;
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
        return "Owner{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}
