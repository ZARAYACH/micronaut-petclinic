package io.micronaut.samples.petclinic.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.samples.petclinic.dto.ClinicDto;
import io.micronaut.samples.petclinic.dto.ClinicNearbyRequest;
import io.micronaut.samples.petclinic.dto.ClinicPolygonRequest;
import io.micronaut.samples.petclinic.model.Clinic;
import io.micronaut.samples.petclinic.service.ClinicService;
import io.micronaut.views.View;

import java.util.List;
import java.util.Map;

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
     * @return the clinic location search view
     */
    @Get("/")
    @View("clinics/findClinics")
    public Map<String, Object> findClinics() {
        return Map.of(
                "clinics", List.of()
        );
    }

    /**
     * Returns clinics near the supplied WGS 84 coordinate.
     *
     * @param request nearby search request
     * @return nearby clinics
     */
    @Post(value = "/nearby", consumes = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClinicDto> nearby(@Body ClinicNearbyRequest request) {
        List<Clinic> clinics = clinicService.findClinicsNear(request.longitude(),
                request.latitude(),
                request.radiusMeters());
        return ClinicDto.from(clinics);
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
        List<Clinic> clinics = clinicService.findClinicsWithinPolygon(
                request.coordinatesAsPointList());
        return ClinicDto.from(clinics);
    }

    /**
     * Returns clinics whose location intersects the supplied line.
     *
     * @param request line search request
     * @return matching clinics
     */
    @Post(value = "/intersects", consumes = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClinicDto> intersectsLine(@Body ClinicPolygonRequest request) {
        List<Clinic> clinics = clinicService.findClinicsIntersectingLine(
                request.coordinatesAsPointList());
        return ClinicDto.from(clinics);
    }
}
