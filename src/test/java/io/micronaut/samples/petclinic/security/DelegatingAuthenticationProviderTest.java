package io.micronaut.samples.petclinic.security;

import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for the custom authentication provider.
 */
@MicronautTest
class DelegatingAuthenticationProviderTest {

    @Inject
    DelegatingAuthenticationProvider<Object> authenticationProvider;

    @Test
    void shouldAuthenticateKnownUserAndReturnAuthorities() {
        AuthenticationResponse response = authenticate("admin@example.com", "password123");

        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.getAuthentication()).isPresent();
        assertThat(response.getAuthentication().orElseThrow().getName()).isEqualTo("admin@example.com");
        assertThat(response.getAuthentication().orElseThrow().getRoles()).containsExactly("ROLE_ADMIN");
    }

    @Test
    void shouldRejectInvalidPassword() {
        assertThatExceptionOfType(AuthenticationException.class)
                .isThrownBy(() -> authenticate("admin@example.com", "bad-password"))
                .satisfies(exception -> {
                    assertThat(exception.getResponse()).isInstanceOf(AuthenticationFailed.class);
                    AuthenticationFailed response = (AuthenticationFailed) exception.getResponse();
                    assertThat(response.getReason()).isEqualTo(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
                });
    }

    @Test
    void shouldRejectUnknownUser() {
        assertThatExceptionOfType(AuthenticationException.class)
                .isThrownBy(() -> authenticate("missing@example.com", "password123"))
                .satisfies(exception -> {
                    assertThat(exception.getResponse()).isInstanceOf(AuthenticationFailed.class);
                    AuthenticationFailed response = (AuthenticationFailed) exception.getResponse();
                    assertThat(response.getReason()).isEqualTo(AuthenticationFailureReason.USER_NOT_FOUND);
                });
    }

    private AuthenticationResponse authenticate(String username, String password) {
        return authenticationProvider.authenticate(
                null,
                new UsernamePasswordCredentials(username, password)
        );
    }
}
