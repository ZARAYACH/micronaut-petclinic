package io.micronaut.samples.petclinic.repository;

import io.micronaut.data.repository.GenericRepository;
import io.micronaut.samples.petclinic.model.shared.Speciality;
import io.micronaut.samples.petclinic.model.shared.VetSpeciality;
import java.util.List;

/**
 * Repository for the vet-speciality join table.
 * Dialect-specific {@code @JdbcRepository} beans extend this interface.
 */
public interface VetSpecialityRepository extends GenericRepository<VetSpeciality, VetSpeciality> {

    /**
     * Saves a join row between a vet and a speciality.
     *
     * @param entity the join row to save
     */
    void save(VetSpeciality entity);

    /**
     * Finds specialities for a vet.
     *
     * @param vetId the vet id
     * @return the specialities associated with the vet
     */
    List<Speciality> findSpecialitiesByVetId(Integer vetId);
}
