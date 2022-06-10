package ru.malygin.indexer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.malygin.indexer.model.entity.Lemma;
import ru.malygin.indexer.repository.LemmaRepository;
import ru.malygin.indexer.service.LemmaService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class LemmaServiceImpl implements LemmaService {

    private final LemmaRepository lemmaRepository;

    @Override
    public Flux<Lemma> findAllBySiteIdAndAppUserId(Long siteId,
                                                   Long appUserId) {
        return lemmaRepository.findAllBySiteIdAndAppUserId(siteId, appUserId);
    }

    @Override
    public Flux<Lemma> saveAll(List<Lemma> lemmas) {
        return lemmaRepository.saveAll(lemmas);
    }

    @Override
    public Mono<Void> deleteAllBySiteIdAndAppUserId(Long siteId,
                                                    Long appUserId) {
        return lemmaRepository.deleteAllBySiteIdAndAppUserId(siteId, appUserId);
    }

    @Override
    public Mono<Long> getCountBySiteIdAndAppUserId(Long siteId,
                                                   Long appUserId) {
        return lemmaRepository.countLemmaBySiteIdAndAppUserId(siteId, appUserId);
    }
}
