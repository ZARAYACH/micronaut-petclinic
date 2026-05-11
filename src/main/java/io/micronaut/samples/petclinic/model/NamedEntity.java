package io.micronaut.samples.petclinic.model;

/**
 * Shared contract for entities with a name.
 */
public sealed interface NamedEntity extends BaseEntity permits Pet, PetType, Specialty {

    /**
     * Returns the display name.
     *
     * @return the entity name
     */
    String name();

    /**
     * JavaBean-style alias for {@link #name()} used by views and framework code.
     *
     * @return the entity name
     */
    default String getName() {
        return name();
    }
}
