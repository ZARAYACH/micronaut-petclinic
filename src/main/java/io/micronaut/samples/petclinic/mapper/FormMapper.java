package io.micronaut.samples.petclinic.mapper;

import io.micronaut.context.annotation.Mapper;
import io.micronaut.samples.petclinic.dto.OwnerForm;
import io.micronaut.samples.petclinic.dto.PetForm;
import io.micronaut.samples.petclinic.dto.VisitForm;
import io.micronaut.samples.petclinic.model.Owner;
import io.micronaut.samples.petclinic.model.Pet;
import io.micronaut.samples.petclinic.model.Visit;
import jakarta.inject.Singleton;

/**
 * Maps web form DTOs to and from the domain model.
 * <p>
 * The form types represent URL-encoded request data, while the model types are
 * immutable records used by the repository and service layers. Micronaut
 * generates the implementations for these methods from the {@link Mapper}
 * annotations, keeping controllers free of manual field-copying logic.
 */
@Singleton
public interface FormMapper {

    @Mapper
    Owner toOwner(OwnerForm form);

    @Mapper
    Owner updateOwner(Owner owner, OwnerForm form);

    @Mapper
    OwnerForm toOwnerForm(Owner owner);

    @Mapper
    Pet toPet(PetForm form);

    @Mapper
    Pet updatePet(Pet pet, PetForm form);

    @Mapper
    PetForm toPetForm(Pet pet);

    @Mapper
    Visit toVisit(VisitForm form);
}
