package io.micronaut.samples.petclinic.repository;

import io.micronaut.data.model.geo.Geometry;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.samples.petclinic.model.Clinic;

import java.util.List;

/**
 * Repository for {@link Clinic} entities.
 * <p>
 * Dialect-specific {@code @JdbcRepository} beans extend this interface.
 */
public interface ClinicRepository extends CrudRepository<Clinic, Integer> {

    /**
     * Finds clinics whose point location is within the supplied geometry.
     *
     * @param geometry the search geometry, typically a polygon
     * @return matching clinics
     */
    List<Clinic> findByLocationGeoWithin(Geometry geometry);

    /**
     * Finds clinics whose point location intersects the supplied geometry.
     *
     * @param geometry the search geometry, typically a polygon
     * @return matching clinics
     */
    List<Clinic> findByLocationGeoIntersects(Geometry geometry);

    /**
     * Finds clinics within the supplied distance of a point.
     *
     * @param geometry the search origin; x is longitude and y is latitude when a point is supplied
     * @param distanceMeters the search radius in meters for WGS 84 data
     * @return nearby clinics
     */
    List<Clinic> findByLocationNear(Geometry geometry, double distanceMeters);
}
