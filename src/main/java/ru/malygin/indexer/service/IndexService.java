package ru.malygin.indexer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.malygin.indexer.model.entity.Index;

public interface IndexService {

    Mono<Index> save(Index index);

    Flux<Index> findAllBySiteIdAndAppUserId(Long siteId,
                                            Long appUserId);

    Mono<Void> deleteAllBySiteIdAndAppUserId(Long siteId,
                                             Long appUserId);

    Mono<Long> getCountBySiteIdAndAppUserId(Long siteId,
                                            Long appUserId);
}
