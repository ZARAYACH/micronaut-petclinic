package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

/**
 * Join table mapping for vets {@literal <->} specialties.
 *
 * The Petclinic schema models this as a pure join table without its own id.
 *
 * @param vetId the vet identifier
 * @param specialtyId the specialty identifier
 */
@MappedEntity("VET_SPECIALTIES")
public record VetSpecialty(
        @MappedProperty("VET_ID")
        Integer vetId,

        @MappedProperty("SPECIALTY_ID")
        Integer specialtyId
) {

    /**
     * Returns the vet identifier.
     *
     * @return the vet id
     */
    public Integer getVetId() {
        return vetId;
    }

    /**
     * Returns the specialty identifier.
     *
     * @return the specialty id
     */
    public Integer getSpecialtyId() {
        return specialtyId;
    }
}
