package io.micronaut.samples.petclinic.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.reactor.config.ReactorConfiguration;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.util.Map;
import java.util.Objects;

/**
 * Controller for handling errors.
 * Provides custom error pages for different HTTP status codes.
 */
@Controller("/error")
@Secured(SecurityRule.IS_ANONYMOUS)
public class ErrorController {

    private final ReactorConfiguration reactorConfiguration;

    /**
     * Creates the error controller.
     */
    public ErrorController(ReactorConfiguration reactorConfiguration) {
        this.reactorConfiguration = reactorConfiguration;
    }

    /**
     * Handle 404 Not Found errors.
     * @param request the original request
     * @return the error view
     */
    @Error(status = io.micronaut.http.HttpStatus.NOT_FOUND, global = true)
    @View("error/404")
    public Map<String, Object> notFound(HttpRequest<?> request) {
        return Map.of(
                "path", request.getPath(),
                "message", "Page not found"
        );
    }

    @Get("/401")
    @View("error/401")
    public Map<String, Object> unauthorised(HttpRequest<?> request) {
        return Map.of(
                "path", request.getPath(),
                "message", "unauthorized"
        );
    }

    /**
     * Handle authorization failures raised by the security filter.
     *
     * @param request the original request
     * @param exception the authorization exception
     * @return the error view with the correct status
     */
    @Error(exception = AuthorizationException.class, global = true)
    public HttpResponse<Map<String, Object>> authorizationFailure(HttpRequest<?> request,
                                                                  AuthorizationException exception) {
        HttpStatus status = exception.isForbidden() ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;
        return HttpResponse.status(status).body(
                Map.of("path", request.getPath(),
                        "message", status.getReason()));
    }

    /**
     * Handle 500 Internal Server Error.
     * @param request the original request
     * @param throwable the exception that occurred
     * @return the error view
     */
    @Error(global = true)
    @View("error/error")
    public Map<String, Object> handleError(HttpRequest<?> request, Throwable throwable) {
        return Map.of(
                "path", request.getPath(),
                "message", Objects.toString(throwable.getMessage(), ""),
                "exception", throwable.getClass().getSimpleName()
        );
    }
}
