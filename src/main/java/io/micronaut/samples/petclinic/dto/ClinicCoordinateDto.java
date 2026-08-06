package io.micronaut.samples.petclinic.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.model.geo.Point;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Coordinate submitted by map-based clinic searches.
 *
 * @param latitude the latitude coordinate
 * @param longitude the longitude coordinate
 */
@Introspected
@Serdeable
public record ClinicCoordinateDto(
        double latitude,
        double longitude
) {

    /**
     * Creates an empty coordinate for framework binding.
     */
    public ClinicCoordinateDto() {
        this(0, 0);
    }

    public Point asPoint() {
        return new Point(this.longitude, this.latitude);
    }
}
