package io.micronaut.samples.petclinic.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.samples.petclinic.model.Vet;
import io.micronaut.samples.petclinic.repository.VetRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link VetController}.
 */
@MicronautTest
class VetControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    EmbeddedServer server;

    @Inject
    VetRepository vetRepository;

    private static MutableHttpRequest<?> formPost(String uri, Map<String, String> form) {
        return HttpRequest.POST(uri, form)
                .contentType(io.micronaut.http.MediaType.APPLICATION_FORM_URLENCODED);
    }

    @SuppressWarnings("unchecked")
    private static HttpResponse<String> exchange(HttpClient client, HttpRequest<?> request) {
        try {
            return client.toBlocking().exchange(request, String.class);
        } catch (HttpClientResponseException e) {
            return (HttpResponse<String>) e.getResponse();
        }
    }

    private static String firstCookie(HttpResponse<?> response) {
        List<String> setCookies = response.getHeaders().getAll(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).isNotEmpty();
        return setCookies.getFirst().split(";", 2)[0];
    }

    @Test
    void shouldReturnVetsJson() {
        HttpResponse<String> response = client.toBlocking()
                .exchange(HttpRequest.GET("/vets/json"), String.class);
        
        assertThat((CharSequence) response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.body()).isNotNull();
        assertThat(response.body()).contains("firstName");
        assertThat(response.body()).contains("lastName");
    }

    @Test
    void shouldReturnVetsHtmlPage() {
        HttpResponse<String> response = client.toBlocking()
                .exchange(HttpRequest.GET("/vets"), String.class);
        
        assertThat((CharSequence) response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.body()).isNotNull();
        assertThat(response.body()).contains("Veterinarians");
    }

    @Test
    void shouldReturnNewVetForm() {
        try (HttpClient noRedirectClient = createNoRedirectClient()) {
            HttpResponse<String> loginResponse = exchange(noRedirectClient, formPost("/login", Map.of(
                    "username", "admin@example.com",
                    "password", "password123"
            )));
            String sessionCookie = firstCookie(loginResponse);

            HttpResponse<String> response = exchange(noRedirectClient, HttpRequest.GET("/vets/new")
                    .header(HttpHeaders.COOKIE, sessionCookie));

            assertThat((CharSequence) response.status()).isEqualTo(HttpStatus.OK);
            assertThat(response.body()).contains("New Veterinarian");
        }
    }

    @Test
    void shouldRejectAnonymousVetCreation() {
        try (HttpClient noRedirectClient = createNoRedirectClient()) {
            HttpResponse<String> response = exchange(noRedirectClient, formPost("/vets/new", Map.of(
                    "firstName", "Anonymous",
                    "lastName", "Vet"
            )));

            assertThat((CharSequence) response.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Test
    void shouldCreateVetWhenAuthenticated() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String firstName = "Test" + suffix;
        String lastName = "Vet" + suffix;

        try (HttpClient noRedirectClient = createNoRedirectClient()) {
            HttpResponse<String> loginResponse = exchange(noRedirectClient, formPost("/login", Map.of(
                    "username", "admin@example.com",
                    "password", "password123"
            )));
            String sessionCookie = firstCookie(loginResponse);

            HttpResponse<String> response = exchange(noRedirectClient, formPost("/vets/new", Map.of(
                    "firstName", firstName,
                    "lastName", lastName
            )).header(HttpHeaders.COOKIE, sessionCookie));

            assertThat(response.status().getCode()).isBetween(300, 399);
            assertThat(response.getHeaders().get(HttpHeaders.LOCATION)).isEqualTo("/vets");
        }

        Iterable<Vet> vets = vetRepository.findAll();
        assertThat(vets).anySatisfy(vet -> {
            assertThat(vet.getFirstName()).isEqualTo(firstName);
            assertThat(vet.getLastName()).isEqualTo(lastName);
        });
    }

    private HttpClient createNoRedirectClient() {
        DefaultHttpClientConfiguration configuration = new DefaultHttpClientConfiguration();
        configuration.setFollowRedirects(false);
        configuration.setExceptionOnErrorStatus(false);
        return HttpClient.create(server.getURL(), configuration);
    }
}
