package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.Transient;

/**
 * Shared contract for people in the domain model.
 */
public sealed interface Person extends BaseEntity permits Owner, Vet {

    String firstName();

    String lastName();

    default String getFirstName() {
        return firstName();
    }

    default String getLastName() {
        return lastName();
    }

    @Transient
    default String getFullName() {
        return firstName() + " " + lastName();
    }
}
