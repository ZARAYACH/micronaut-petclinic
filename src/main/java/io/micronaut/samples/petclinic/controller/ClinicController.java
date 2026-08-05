package io.micronaut.samples.petclinic.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.model.geo.Point;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.samples.petclinic.dto.ClinicDto;
import io.micronaut.samples.petclinic.dto.ClinicPolygonRequest;
import io.micronaut.samples.petclinic.service.ClinicService;
import io.micronaut.views.View;

import java.util.Map;
import java.util.List;

/**
 * Controller for clinic geospatial lookup examples.
 */
@Controller("/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    /**
     * Creates the controller with the service facade.
     *
     * @param clinicService the facade used for clinic lookups
     */
    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    /**
     * Displays the clinic geospatial search page.
     *
     * @param mode the search mode
     * @param latitude the latitude coordinate for near searches
     * @param longitude the longitude coordinate for near searches
     * @param radiusMeters the search radius in meters
     * @param minLatitude the minimum latitude for bounding-box searches
     * @param minLongitude the minimum longitude for bounding-box searches
     * @param maxLatitude the maximum latitude for bounding-box searches
     * @param maxLongitude the maximum longitude for bounding-box searches
     * @return the clinic location search view
     */
    @Get
    @View("clinics/findClinics")
    public Map<String, Object> findClinics(@QueryValue(defaultValue = "near") String mode,
                                           @QueryValue(defaultValue = "43.0731") double latitude,
                                           @QueryValue(defaultValue = "-89.4012") double longitude,
                                           @QueryValue(defaultValue = "5000") double radiusMeters,
                                           @QueryValue(defaultValue = "43.0753") double minLatitude,
                                           @QueryValue(defaultValue = "-89.5186") double minLongitude,
                                           @QueryValue(defaultValue = "43.1836") double maxLatitude,
                                           @QueryValue(defaultValue = "-89.2137") double maxLongitude) {
        List<ClinicDto> clinics = (switch (mode) {
            case "within" -> clinicService.findClinicsWithinBounds(minLongitude, minLatitude, maxLongitude, maxLatitude);
            case "intersects" -> clinicService.findClinicsIntersectingBoundary(minLongitude, minLatitude, maxLongitude, maxLatitude);
            default -> clinicService.findClinicsNear(longitude, latitude, radiusMeters);
        }).stream().map(ClinicDto::from).toList();
        return Map.of(
                "clinics", clinics,
                "mode", mode,
                "latitude", latitude,
                "longitude", longitude,
                "radiusMeters", radiusMeters,
                "minLatitude", minLatitude,
                "minLongitude", minLongitude,
                "maxLatitude", maxLatitude,
                "maxLongitude", maxLongitude
        );
    }

    /**
     * Returns clinics near the supplied WGS 84 coordinate.
     *
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @param radiusMeters the search radius in meters
     * @return nearby clinics
     */
    @Get("/nearby")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClinicDto> nearby(@QueryValue double latitude,
                                  @QueryValue double longitude,
                                  @QueryValue(defaultValue = "5000") double radiusMeters) {
        return clinicService.findClinicsNear(longitude, latitude, radiusMeters).stream()
                .map(ClinicDto::from)
                .toList();
    }

    /**
     * Returns clinics whose location falls within the supplied bounding box.
     *
     * @param minLatitude minimum latitude
     * @param minLongitude minimum longitude
     * @param maxLatitude maximum latitude
     * @param maxLongitude maximum longitude
     * @return matching clinics
     */
    @Get("/within")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClinicDto> within(@QueryValue double minLatitude,
                                  @QueryValue double minLongitude,
                                  @QueryValue double maxLatitude,
                                  @QueryValue double maxLongitude) {
        return clinicService.findClinicsWithinBounds(minLongitude, minLatitude, maxLongitude, maxLatitude).stream()
                .map(ClinicDto::from)
                .toList();
    }

    /**
     * Returns clinics whose location falls within the supplied polygon.
     *
     * @param request polygon search request
     * @return matching clinics
     */
    @Post(value = "/within", consumes = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClinicDto> withinPolygon(@Body ClinicPolygonRequest request) {
        return clinicService.findClinicsWithinPolygon(request.coordinates().stream()
                        .map(coordinate -> new Point(coordinate.longitude(), coordinate.latitude()))
                        .toList()).stream()
                .map(ClinicDto::from)
                .toList();
    }

    /**
     * Returns clinics whose location intersects the supplied bounding box boundary.
     *
     * @param minLatitude minimum latitude
     * @param minLongitude minimum longitude
     * @param maxLatitude maximum latitude
     * @param maxLongitude maximum longitude
     * @return matching clinics
     */
    @Get("/intersects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClinicDto> intersects(@QueryValue double minLatitude,
                                      @QueryValue double minLongitude,
                                      @QueryValue double maxLatitude,
                                      @QueryValue double maxLongitude) {
        return clinicService.findClinicsIntersectingBoundary(minLongitude, minLatitude, maxLongitude, maxLatitude).stream()
                .map(ClinicDto::from)
                .toList();
    }

    /**
     * Returns clinics whose location intersects the supplied polygon boundary.
     *
     * @param request polygon search request
     * @return matching clinics
     */
    @Post(value = "/intersects", consumes = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClinicDto> intersectsPolygon(@Body ClinicPolygonRequest request) {
        return clinicService.findClinicsIntersectingBoundary(request.coordinates().stream()
                        .map(coordinate -> new Point(coordinate.longitude(), coordinate.latitude()))
                        .toList()).stream()
                .map(ClinicDto::from)
                .toList();
    }
}
