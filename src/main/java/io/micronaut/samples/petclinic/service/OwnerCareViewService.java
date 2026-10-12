package io.micronaut.samples.petclinic.service;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.server.exceptions.NotFoundException;
import io.micronaut.samples.petclinic.oracle.model.OwnerCareView;
import io.micronaut.samples.petclinic.repository.OwnerCareViewRepository;
import jakarta.inject.Singleton;

/**
 * Loads and updates owner-care documents through Micronaut Data's JSON view
 * repository while protecting their shape and Oracle ETAG.
 */
@Singleton
@Requires(env = "oracle")
public class OwnerCareViewService {

    private final OwnerCareViewRepository repository;

    public OwnerCareViewService(OwnerCareViewRepository repository) {
        this.repository = repository;
    }

    public OwnerCareView findById(Integer ownerId) {
        return repository.findById(ownerId).orElseThrow(NotFoundException::new);
    }

    /**
     * Updates an existing document while preserving nested membership and IDs.
     *
     * @param ownerId path owner identifier
     * @param updated edited document
     * @return the persisted document with a fresh ETAG
     */
    public OwnerCareView update(Integer ownerId, OwnerCareView updated) {
        if (!ownerId.equals(updated.id())) {
            throw new IllegalArgumentException("The document _id must match the owner in the URL.");
        }
        return repository.update(updated);
    }

}
