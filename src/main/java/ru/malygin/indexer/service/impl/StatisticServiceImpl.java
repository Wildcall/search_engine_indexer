package ru.malygin.indexer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.malygin.indexer.model.entity.Statistic;
import ru.malygin.indexer.repository.StatisticRepository;
import ru.malygin.indexer.service.StatisticService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Override
    public Mono<Statistic> save(Statistic statistic) {
        return statisticRepository.save(statistic);
    }
}
