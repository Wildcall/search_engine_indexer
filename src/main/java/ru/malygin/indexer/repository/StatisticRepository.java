package ru.malygin.indexer.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.malygin.indexer.model.entity.Statistic;

public interface StatisticRepository extends ReactiveCrudRepository<Statistic, Long> {
}
