package io.micronaut.samples.petclinic.service;

import io.micronaut.samples.petclinic.model.Clinic;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Geospatial integration tests for {@link ClinicService}.
 */
@MicronautTest
class ClinicServiceGeoTest {

    @Inject
    ClinicService clinicService;

    @Test
    void shouldFindClinicsNearPoint() {
        Collection<Clinic> clinics = clinicService.findClinicsNear(-89.3840, 43.0745, 0.003);
        assertThat(clinics).isNotEmpty();
        assertThat(clinics).extracting(Clinic::name)
                .contains("Downtown Madison Pet Clinic", "Capitol Square Pet Clinic")
                .doesNotContain("University Pet Clinic", "Milwaukee Pet Clinic");
    }

    @Test
    void shouldExpandClinicsAsNearbyRadiusGrows() {
        Collection<Clinic> smallRadiusClinics = clinicService.findClinicsNear(-89.3840, 43.0745, 0.003);
        Collection<Clinic> largerRadiusClinics = clinicService.findClinicsNear(-89.3840, 43.0745, 0.05);

        assertThat(smallRadiusClinics).isNotEmpty();
        assertThat(largerRadiusClinics.size()).isGreaterThan(smallRadiusClinics.size());
        assertThat(smallRadiusClinics).extracting(Clinic::name)
                .contains("Downtown Madison Pet Clinic", "Capitol Square Pet Clinic");
        assertThat(largerRadiusClinics).extracting(Clinic::name)
                .contains("University Pet Clinic", "East Madison Pet Clinic");
    }

    @Test
    void shouldFindClinicsWithinBounds() {
        Collection<Clinic> clinics = clinicService.findClinicsWithinBounds(-89.55, 43.00, -89.20, 43.20);
        assertThat(clinics).isNotEmpty();
        assertThat(clinics).extracting(Clinic::name)
                .contains("Downtown Madison Pet Clinic", "Monona Pet Clinic")
                .doesNotContain("Milwaukee Pet Clinic");
    }

    @Test
    void shouldFindClinicsIntersectingBoundary() {
        Collection<Clinic> clinics = clinicService.findClinicsIntersectingBoundary(-89.5186, 43.0753, -89.2137, 43.1836);
        assertThat(clinics).isNotEmpty();
        assertThat(clinics).extracting(Clinic::name)
                .contains("West Madison Pet Clinic", "Sun Prairie Pet Clinic")
                .doesNotContain("Downtown Madison Pet Clinic", "Janesville Pet Clinic");
    }
}
