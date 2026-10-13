package io.micronaut.samples.petclinic.service;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.data.exceptions.OptimisticLockException;
import io.micronaut.http.server.exceptions.NotFoundException;
import io.micronaut.samples.petclinic.oracle.model.OwnerCareView;
import io.micronaut.samples.petclinic.repository.OwnerCareViewRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads and updates owner-care documents through Micronaut Data's JSON view
 * repository. The view's operation annotations protect nested membership.
 */
@Singleton
@Requires(env = "oracle")
public class OwnerCareViewService {

    private static final Logger log = LoggerFactory.getLogger(OwnerCareViewService.class);
    private final OwnerCareViewRepository repository;

    public OwnerCareViewService(OwnerCareViewRepository repository) {
        this.repository = repository;
    }

    /**
     * Finds OwnerCareView by id.
     * @param ownerId owner id.
     * @return OwnerCareView matching the provided owner id.
     */
    public OwnerCareView findById(Integer ownerId) {
        return repository.findById(ownerId).orElseThrow(NotFoundException::new);
    }

    /**
     * Updates an existing document while preserving nested membership and IDs.
     *
     * @param updated edited document
     * @return the persisted document with a fresh ETAG
     */
    public OwnerCareView update(OwnerCareView updated) {
        try {
            repository.update(updated);
            return findById(updated.id());
        } catch (OptimisticLockException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Oracle JSON duality view rejected the update", e);
            throw new IllegalArgumentException("The requested operation is not allowed.", e);
        }
    }

}
