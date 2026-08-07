package io.micronaut.samples.petclinic.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Produces;
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

    /**
     * Handle authorization failures raised by the security filter.
     *
     * @param request the original request
     * @return the error view with the correct status
     */
    @Error(status = HttpStatus.UNAUTHORIZED, global = true)
    @View("error/401")
    public Map<String, Object> unauthorized(HttpRequest<?> request) {
        return Map.of(
                "path", request.getPath(),
                "message", "Unauthorized"
        );
    }
    /**
     * Handle authorization failures raised by the security filter.
     *
     * @param request the original request
     * @return the error view with the correct status
     */
    @Error(status = HttpStatus.FORBIDDEN, global = true)
    @View("error/401")
    public Map<String, Object> forbidden(HttpRequest<?> request) {
        return Map.of(
                "path", request.getPath(),
                "message", "Forbidden"
        );
    }

    /**
     * Handle 500 Internal Server Error.
     * @param request the original request
     * @return the error view
     */
    @Error(status = HttpStatus.INTERNAL_SERVER_ERROR, global = true)
    @View("error/error")
    public Map<String, Object> internalServerError(HttpRequest<?> request) {
        return Map.of(
                "path", request.getPath(),
                "message", "Internal Server Error",
                "exception", "InternalServerError"
        );
    }
}
