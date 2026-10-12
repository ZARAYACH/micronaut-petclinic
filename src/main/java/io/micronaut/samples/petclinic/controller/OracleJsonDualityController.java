package io.micronaut.samples.petclinic.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.exceptions.OptimisticLockException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.samples.petclinic.oracle.model.OwnerCareView;
import io.micronaut.samples.petclinic.service.OwnerCareViewService;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Controller("/owners/{ownerId}/care-record")
@Requires(env = "oracle")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
class OracleJsonDualityController {

    private final OwnerCareViewService service;

    public OracleJsonDualityController(OwnerCareViewService service) {
        this.service = service;
    }

    @Get
    public OwnerCareView getDocument(@PathVariable Integer ownerId) {
        return service.findById(ownerId);
    }

    @Error(exception = OptimisticLockException.class)
    public HttpResponse<Map<String, String>> staleDocument(OptimisticLockException error, @PathVariable String ownerId) {
        return HttpResponse.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "This owner #" + ownerId + " record changed after it was loaded. Reload it before saving."));
    }

    @Put(consumes = MediaType.APPLICATION_JSON)
    public OwnerCareView updateDocument(@PathVariable @NonNull Integer ownerId,
                                        @NotNull @Valid @Body OwnerCareView document) {
        return service.update(ownerId, document);
    }
}
