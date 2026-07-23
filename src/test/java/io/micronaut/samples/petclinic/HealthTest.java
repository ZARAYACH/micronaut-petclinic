package io.micronaut.samples.petclinic;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class HealthTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Value("${micronaut.application.name}")
    String appName;

    @Test
    public void healthEndpointExposed() {
        HttpStatus status = client.toBlocking().retrieve(HttpRequest.GET("/health"), HttpStatus.class);
        assertEquals(HttpStatus.OK, status);
    }

    @Test
    public void infoEndpointExposed() {
        InfoDto infoDto = client.toBlocking().retrieve(HttpRequest.GET("/info"), InfoDto.class);
        assertEquals(infoDto.application.get("name"), appName);
    }

    @Introspected
    record InfoDto(
            Map<String, String> application
    ) {
    }
}
