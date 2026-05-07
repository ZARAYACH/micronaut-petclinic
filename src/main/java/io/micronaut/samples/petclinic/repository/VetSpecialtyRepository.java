package io.micronaut.samples.petclinic.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.GenericRepository;
import io.micronaut.samples.petclinic.model.Specialty;
import io.micronaut.samples.petclinic.model.VetSpecialty;
import java.util.List;

// Dialect-specific @JdbcRepository beans extend this interface.
public interface VetSpecialtyRepository extends GenericRepository<VetSpecialty, VetSpecialty> {

    void save(VetSpecialty entity);

    List<Specialty> findSpecialtiesByVetId(Integer vetId);
}
