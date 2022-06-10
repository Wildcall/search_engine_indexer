package ru.malygin.indexer.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.malygin.indexer.model.entity.Index;

public interface IndexRepository extends ReactiveCrudRepository<Index, Long> {

    Flux<Index> findAllBySiteIdAndAppUserId(Long siteId,
                                            Long appUserId);

    Mono<Void> deleteAllBySiteIdAndAppUserId(Long siteId,
                                             Long appUserId);

    Mono<Long> countIndexBySiteIdAndAppUserId(Long siteId,
                                              Long appUserId);
}
