package ru.malygin.indexer.indexer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.malygin.indexer.indexer.util.AlphabetType;
import ru.malygin.indexer.indexer.util.Lemmantisator;
import ru.malygin.indexer.model.Page;
import ru.malygin.indexer.model.Task;
import ru.malygin.indexer.model.entity.Index;
import ru.malygin.indexer.model.entity.Lemma;
import ru.malygin.indexer.service.IndexService;
import ru.malygin.indexer.service.LemmaService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageParser {

    private final Long siteId;
    private final Long appUserId;
    private final Task task;
    private final LemmaService lemmaService;
    private final IndexService indexService;
    private final Map<String, Integer> wordFreq = new ConcurrentHashMap<>();
    @Getter
    private final AtomicInteger savedIndexes = new AtomicInteger(0);
    @Getter
    private final AtomicInteger createdLemmas = new AtomicInteger(0);

    public void parsePage(Page page) {
        if (page.getContent() != null) {
            Map<String, Double> wordRankMap = Lemmantisator.parsePage(page.getContent(),
                                                                      task.getSelectorWeight(),
                                                                      AlphabetType.CYRILLIC);
            if (wordRankMap != null) {
                Flux
                        .fromStream(wordRankMap
                                            .keySet()
                                            .stream()
                                            .map(word -> new Index(null,
                                                                   siteId,
                                                                   appUserId,
                                                                   wordRankMap.get(word),
                                                                   page.getPath(),
                                                                   word)))
                        .subscribe(index -> indexService
                                .save(index)
                                .doOnSuccess(i -> savedIndexes.incrementAndGet())
                                .subscribe(sink -> wordFreq.merge(sink.getWord(), 1, (freq, one) -> freq + 1)));
            }
        }
    }

    public Flux<Lemma> saveLemmas() {
        return lemmaService.saveAll(wordFreq
                                            .keySet()
                                            .stream()
                                            .map(word -> {
                                                createdLemmas.incrementAndGet();
                                                return new Lemma(null,
                                                                 siteId,
                                                                 appUserId,
                                                                 word,
                                                                 wordFreq.get(word));
                                            })
                                            .collect(Collectors.toList()));
    }

    @Component
    @RequiredArgsConstructor
    public static class Builder {

        private final LemmaService lemmaService;
        private final IndexService indexService;
        private Long siteId;
        private Long appUserId;
        private Task task;

        public Builder siteId(Long siteId) {
            this.siteId = siteId;
            return this;
        }

        public Builder appUserId(Long appUserId) {
            this.appUserId = appUserId;
            return this;
        }

        public Builder task(Task task) {
            this.task = task;
            return this;
        }

        public PageParser build() {
            return new PageParser(siteId, appUserId, task, lemmaService, indexService);
        }
    }
}
