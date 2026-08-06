package io.micronaut.samples.petclinic.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.model.geo.Point;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/**
 * Request body for polygon-based clinic searches.
 *
 * @param coordinates polygon shell coordinates
 */
@Introspected
@Serdeable
public record ClinicPolygonRequest(
        List<ClinicCoordinateDto> coordinates
) {

    /**
     * Creates an empty request for framework binding.
     */
    public ClinicPolygonRequest() {
        this(null);
    }


    public List<Point> coordinatesAsPointList() {
        return coordinates.stream().map(ClinicCoordinateDto::asPoint).toList();

    }
}
