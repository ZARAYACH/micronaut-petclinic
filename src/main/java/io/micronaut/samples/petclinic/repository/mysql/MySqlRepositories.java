package io.micronaut.samples.petclinic.repository.mysql;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.samples.petclinic.model.*;
import io.micronaut.samples.petclinic.repository.*;
import java.util.Collection;
import java.util.List;

public final class MySqlRepositories {
    private MySqlRepositories() {
    }

    @Requires(env = "mysql")
    @JdbcRepository(dialect = Dialect.MYSQL)
    public interface MySqlOwnerRepository extends OwnerRepository {
    }

    @Requires(env = "mysql")
    @JdbcRepository(dialect = Dialect.MYSQL)
    public interface MySqlPetRepository extends PetRepository {
        @Override
        @Query(value = "SELECT p.* FROM PETS p WHERE p.OWNER_ID = :ownerId ORDER BY p.NAME", nativeQuery = true)
        Collection<Pet> findByOwnerId(Integer ownerId);

        @Override
        @Query(value = "SELECT p.* FROM PETS p WHERE p.OWNER_ID IN (:ownerIds) ORDER BY p.OWNER_ID, p.NAME", nativeQuery = true)
        List<Pet> findByOwnerIdIn(List<Integer> ownerIds);
    }

    @Requires(env = "mysql")
    @JdbcRepository(dialect = Dialect.MYSQL)
    public interface MySqlPetTypeRepository extends PetTypeRepository {
    }

    @Requires(env = "mysql")
    @JdbcRepository(dialect = Dialect.MYSQL)
    public interface MySqlSpecialtyRepository extends SpecialtyRepository {
    }

    @Requires(env = "mysql")
    @JdbcRepository(dialect = Dialect.MYSQL)
    public interface MySqlVetRepository extends VetRepository {
        @Override
        @Query(value = "SELECT v.* FROM VETS v ORDER BY v.LAST_NAME", nativeQuery = true)
        Collection<Vet> findAllWithSpecialties();
    }

    @Requires(env = "mysql")
    @JdbcRepository(dialect = Dialect.MYSQL)
    public interface MySqlVisitRepository extends VisitRepository {
        @Override
        @Query(value = "SELECT v.* FROM VISITS v WHERE v.PET_ID = :petId ORDER BY v.VISIT_DATE DESC", nativeQuery = true)
        Collection<Visit> findByPetId(Integer petId);
    }

    @Requires(env = "mysql")
    @JdbcRepository(dialect = Dialect.MYSQL)
    public interface MySqlVetSpecialtyRepository extends VetSpecialtyRepository {
        @Override
        @Query(value = "SELECT s.* FROM SPECIALTIES s JOIN VET_SPECIALTIES vs ON vs.SPECIALTY_ID = s.id WHERE vs.VET_ID = :vetId ORDER BY s.NAME", nativeQuery = true)
        List<Specialty> findSpecialtiesByVetId(Integer vetId);
    }
}
