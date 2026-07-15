package io.micronaut.samples.petclinic.security;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.samples.petclinic.model.UserState;
import io.micronaut.samples.petclinic.service.AuthoritiesFetcher;
import io.micronaut.samples.petclinic.service.UserFetcher;
import io.micronaut.samples.petclinic.utils.PasswordEncoder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestReactiveAuthenticationProvider;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static io.micronaut.security.authentication.AuthenticationFailureReason.ACCOUNT_EXPIRED;
import static io.micronaut.security.authentication.AuthenticationFailureReason.ACCOUNT_LOCKED;
import static io.micronaut.security.authentication.AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH;
import static io.micronaut.security.authentication.AuthenticationFailureReason.PASSWORD_EXPIRED;
import static io.micronaut.security.authentication.AuthenticationFailureReason.USER_DISABLED;
import static io.micronaut.security.authentication.AuthenticationFailureReason.USER_NOT_FOUND;

/**
 * Micronaut Security authentication provider backed by the application's user tables.
 *
 * @param <B> the HTTP request body type
 */
@Singleton
class DelegatingAuthenticationProvider<B> implements HttpRequestReactiveAuthenticationProvider<B> {

    private final UserFetcher userFetcher;
    private final PasswordEncoder passwordEncoder;
    private final AuthoritiesFetcher authoritiesFetcher;
    private final Scheduler scheduler;

    /**
     * Creates the provider with services used to load and verify users.
     *
     * @param userFetcher loads persisted user state
     * @param passwordEncoder verifies submitted passwords
     * @param authoritiesFetcher loads user authorities after successful authentication
     * @param executorService blocking executor used for database and password hashing work
     */
    DelegatingAuthenticationProvider(UserFetcher userFetcher,
                                     PasswordEncoder passwordEncoder,
                                     AuthoritiesFetcher authoritiesFetcher,
                                     @Named(TaskExecutors.BLOCKING) ExecutorService executorService) {
        this.userFetcher = userFetcher;
        this.passwordEncoder = passwordEncoder;
        this.authoritiesFetcher = authoritiesFetcher;
        this.scheduler = Schedulers.fromExecutorService(executorService);
    }

    /**
     * Authenticates a user name and password request.
     *
     * @param requestContext the current HTTP request, when one is available
     * @param authenticationRequest the submitted user name and password
     * @return a publisher that emits a successful authentication or fails with an authentication exception
     */
    @Override
    @NonNull
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable HttpRequest<B> requestContext,
            @NonNull AuthenticationRequest<String, String> authenticationRequest
    ) {
        return Flux.<AuthenticationResponse>create(emitter -> {
            UserState user = fetchUserState(authenticationRequest);
            AuthenticationFailed authenticationFailed = validate(user, authenticationRequest);
            if (authenticationFailed != null) {
                emitter.error(new AuthenticationException(authenticationFailed));
            } else {
                emitter.next(createSuccessfulAuthenticationResponse(user));
                emitter.complete();
            }
        }, FluxSink.OverflowStrategy.ERROR).subscribeOn(scheduler);
    }

    /**
     * Validates account state and password credentials.
     *
     * @param user the loaded user state, or {@code null} when the user was not found
     * @param authenticationRequest the submitted credentials
     * @return the failure reason, or {@code null} when validation succeeds
     */
    private AuthenticationFailed validate(UserState user, AuthenticationRequest<?, ?> authenticationRequest) {
        AuthenticationFailed authenticationFailed = null;
        if (user == null) {
            authenticationFailed = new AuthenticationFailed(USER_NOT_FOUND);

        } else if (!user.isEnabled()) {
            authenticationFailed = new AuthenticationFailed(USER_DISABLED);

        } else if (user.isAccountExpired()) {
            authenticationFailed = new AuthenticationFailed(ACCOUNT_EXPIRED);

        } else if (user.isAccountLocked()) {
            authenticationFailed = new AuthenticationFailed(ACCOUNT_LOCKED);

        } else if (user.isPasswordExpired()) {
            authenticationFailed = new AuthenticationFailed(PASSWORD_EXPIRED);

        } else if (!passwordEncoder.matches(authenticationRequest.getSecret().toString(), user.getPassword())) {
            authenticationFailed = new AuthenticationFailed(CREDENTIALS_DO_NOT_MATCH);
        }

        return authenticationFailed;
    }

    /**
     * Loads user state for the submitted identity.
     *
     * @param authRequest the submitted credentials
     * @return the user state, or {@code null} when the user does not exist
     */
    private UserState fetchUserState(AuthenticationRequest<?, ?> authRequest) {
        final Object username = authRequest.getIdentity();
        return userFetcher.findByUsername(username.toString()).orElse(null);
    }

    /**
     * Builds a Micronaut Security response for a verified user.
     *
     * @param user the authenticated user state
     * @return an authenticated response containing the user's authorities
     */
    private AuthenticationResponse createSuccessfulAuthenticationResponse(UserState user) {
        List<String> authorities = authoritiesFetcher.findAuthoritiesByUsername(user.getUsername());
        return AuthenticationResponse.success(user.getUsername(), authorities);
    }
}
