package io.micronaut.samples.petclinic.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.model.geo.Point;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/**
 * Request body for clinic searches that submit an ordered coordinate list.
 *
 * @param coordinates coordinates used to build a polygon or line
 */
@Introspected
@Serdeable
public record ClinicCoordinatesRequest(
        List<ClinicCoordinateDto> coordinates
) {

    /**
     * Creates an empty request for framework binding.
     */
    public ClinicCoordinatesRequest() {
        this(null);
    }

    /**
     * Converts submitted latitude/longitude coordinates to Micronaut Data points.
     *
     * @return coordinate list as points where x is longitude and y is latitude
     */
    public List<Point> coordinatesAsPointList() {
        return coordinates.stream().map(ClinicCoordinateDto::asPoint).toList();
    }
}
