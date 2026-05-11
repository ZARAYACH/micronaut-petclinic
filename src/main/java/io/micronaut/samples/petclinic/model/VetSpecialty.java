package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

/**
 * Join table mapping for vets <-> specialties.
 *
 * The Petclinic schema models this as a pure join table without its own id.
 */
@MappedEntity("VET_SPECIALTIES")
public record VetSpecialty(
        @MappedProperty("VET_ID")
        Integer vetId,

        @MappedProperty("SPECIALTY_ID")
        Integer specialtyId
) {

    public Integer getVetId() {
        return vetId;
    }

    public Integer getSpecialtyId() {
        return specialtyId;
    }
}
