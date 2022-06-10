package ru.malygin.indexer.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.malygin.indexer.model.entity.Lemma;

public interface LemmaRepository extends ReactiveCrudRepository<Lemma, Long> {

    Flux<Lemma> findAllBySiteIdAndAppUserId(Long siteId,
                                            Long appUserId);

    Mono<Void> deleteAllBySiteIdAndAppUserId(Long siteId,
                                             Long appUserId);

    Mono<Long> countLemmaBySiteIdAndAppUserId(Long siteId,
                                              Long appUserId);
}
