package io.micronaut.samples.petclinic.service;

import io.micronaut.data.model.vector.FloatVector;
import io.micronaut.data.model.vector.search.Score;
import io.micronaut.data.model.vector.search.SearchResult;
import io.micronaut.data.model.vector.search.SearchResults;
import io.micronaut.samples.petclinic.model.PetCareChunk;
import io.micronaut.samples.petclinic.repository.PetCareChunkRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the retrieval service without requiring an Oracle instance.
 */
class PetCareKnowledgeServiceTest {

    @Test
    void returnsNoResultsForBlankQueriesWithoutCallingTheRepository() {
        AtomicInteger repositoryCalls = new AtomicInteger();
        PetCareChunkRepository repository = repositoryReturning(repositoryCalls, null);
        PetCareKnowledgeService service = new PetCareKnowledgeService(
                repository,
                query -> {
                    throw new AssertionError("A blank query must not be embedded");
                }
        );

        assertThat(service.search("  ")).isEmpty();
        assertThat(repositoryCalls.get()).isZero();
    }

    @Test
    void mapsRankedChunkResultsAndPreservesScores() {
        PetCareChunk chunk = new PetCareChunk(
                7,
                1,
                "Pet emergency guide",
                "Pet Clinic demonstration notes",
                "poison",
                "all",
                1,
                "Call a veterinarian after a possible toxic exposure.",
                new FloatVector(new float[]{1.0f})
        );
        AtomicInteger repositoryCalls = new AtomicInteger();
        AtomicReference<String> embeddedQuery = new AtomicReference<>();
        PetCareChunkRepository repository = repositoryReturning(
                repositoryCalls,
                new SearchResult<>(chunk, new Score(0.21), new io.micronaut.data.model.vector.search.Similarity(0.89))
        );
        PetCareKnowledgeService service = new PetCareKnowledgeService(
                repository,
                query -> {
                    embeddedQuery.set(query);
                    return new FloatVector(new float[]{1.0f});
                }
        );

        var results = service.search("What should I do after poisoning?");

        assertThat(repositoryCalls).hasValue(1);
        assertThat(embeddedQuery).hasValue("What should I do after poisoning?");
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().id()).isEqualTo(7);
        assertThat(results.getFirst().documentTitle()).isEqualTo("Pet emergency guide");
        assertThat(results.getFirst().topic()).isEqualTo("poison");
        assertThat(results.getFirst().distance()).isEqualTo(0.21);
        assertThat(results.getFirst().similarity()).isEqualTo(0.89);
    }

    private static PetCareChunkRepository repositoryReturning(AtomicInteger calls,
                                                               SearchResult<PetCareChunk> result) {
        return (PetCareChunkRepository) Proxy.newProxyInstance(
                PetCareChunkRepository.class.getClassLoader(),
                new Class<?>[]{PetCareChunkRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("searchTop2ByEmbeddingNear")) {
                        calls.incrementAndGet();
                        if (result == null) {
                            return SearchResults.of(List.of());
                        }
                        return SearchResults.of(List.of(result));
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
