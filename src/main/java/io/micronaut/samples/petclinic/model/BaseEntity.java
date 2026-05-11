package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.Transient;

import java.util.Objects;

/**
 * Shared contract for persistent entities.
 */
public sealed interface BaseEntity permits NamedEntity, Person, Visit {

    Integer id();

    default Integer getId() {
        return id();
    }

    @Transient
    default boolean isNew() {
        return id() == null;
    }

    static boolean entityEquals(BaseEntity entity, Object other) {
        if (entity == other) {
            return true;
        }
        if (other == null || entity.getClass() != other.getClass()) {
            return false;
        }
        BaseEntity that = (BaseEntity) other;
        return Objects.equals(entity.id(), that.id());
    }

    static int entityHashCode(BaseEntity entity) {
        return Objects.hash(entity.id());
    }
}
