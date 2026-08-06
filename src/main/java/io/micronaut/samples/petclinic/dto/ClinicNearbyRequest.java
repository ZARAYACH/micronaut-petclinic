package io.micronaut.samples.petclinic.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Request body for nearby clinic searches.
 *
 * @param latitude the latitude coordinate
 * @param longitude the longitude coordinate
 * @param radiusMeters the search radius in meters
 */
@Introspected
@Serdeable
public record ClinicNearbyRequest(
        double latitude,
        double longitude,
        double radiusMeters
) {

    /**
     * Creates an empty request for framework binding.
     */
    public ClinicNearbyRequest() {
        this(0, 0, 0);
    }
}
