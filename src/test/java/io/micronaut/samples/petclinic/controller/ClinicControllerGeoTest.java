package io.micronaut.samples.petclinic.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Geospatial controller integration tests.
 */
@MicronautTest
class ClinicControllerGeoTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void shouldReturnClinicsWithinBoundingBoxAsJson() {
        HttpResponse<String> response = client.toBlocking()
                .exchange(HttpRequest.GET("/clinics/within?minLatitude=43.00&minLongitude=-89.55&maxLatitude=43.20&maxLongitude=-89.20"), String.class);

        assertThat((CharSequence) response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.body()).contains("Downtown Madison Pet Clinic");
        assertThat(response.body()).doesNotContain("Milwaukee Pet Clinic");
    }

    @Test
    void shouldReturnClinicsIntersectingBoundingBoxAsJson() {
        HttpResponse<String> response = client.toBlocking()
                .exchange(HttpRequest.GET("/clinics/intersects?minLatitude=43.00&minLongitude=-89.55&maxLatitude=43.20&maxLongitude=-89.20"), String.class);

        assertThat((CharSequence) response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.body()).contains("West Madison Pet Clinic");
        assertThat(response.body()).contains("Sun Prairie Pet Clinic");
        assertThat(response.body()).doesNotContain("Janesville Pet Clinic");
    }

    @Test
    void shouldReturnNearbyClinicsAsJson() {
        HttpResponse<String> response = client.toBlocking()
                .exchange(HttpRequest.GET("/clinics/nearby?latitude=43.0745&longitude=-89.3840&radiusMeters=0.003"), String.class);

        assertThat((CharSequence) response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.body()).contains("Downtown Madison Pet Clinic");
        assertThat(response.body()).contains("Capitol Square Pet Clinic");
        assertThat(response.body()).contains("\"latitude\":43.0748");
        assertThat(response.body()).doesNotContain("University Pet Clinic");
    }
}
