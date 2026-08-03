package io.micronaut.samples.petclinic.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.samples.petclinic.model.Clinic;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Controller-facing clinic projection used by both HTML and JSON responses.
 *
 * @param id the clinic identifier
 * @param name the clinic name
 * @param address the clinic street address
 * @param city the clinic city
 * @param latitude the clinic latitude
 * @param longitude the clinic longitude
 */
@Introspected
@Serdeable
public record ClinicDto(
        Integer id,
        String name,
        String address,
        String city,
        double latitude,
        double longitude
) {

    /**
     * Maps a persisted clinic entity to a DTO.
     *
     * @param clinic the persisted clinic
     * @return the DTO projection
     */
    public static ClinicDto from(Clinic clinic) {
        return new ClinicDto(
                clinic.id(),
                clinic.name(),
                clinic.address(),
                clinic.city(),
                clinic.location().y(),
                clinic.location().x()
        );
    }
}
