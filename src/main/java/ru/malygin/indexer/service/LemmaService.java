package ru.malygin.indexer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.malygin.indexer.model.entity.Lemma;

import java.util.List;

public interface LemmaService {

    Flux<Lemma> findAllBySiteIdAndAppUserId(Long siteId,
                                            Long appUserId);

    public Flux<Lemma> saveAll(List<Lemma> lemmas);

    Mono<Void> deleteAllBySiteIdAndAppUserId(Long siteId,
                                             Long appUserId);

    Mono<Long> getCountBySiteIdAndAppUserId(Long siteId,
                                              Long appUserId);
}
