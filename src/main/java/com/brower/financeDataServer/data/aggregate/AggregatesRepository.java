package com.brower.financeDataServer.data.aggregate;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AggregatesRepository extends ReactiveMongoRepository<Aggregate, String> {
    @Query("{ 'ticker': ?0 }")
    Flux<Aggregate> findByTicker(String ticker);

    @Query("{ 'ticker': {$in: ?0}}")
    Flux<Aggregate> findTickersInTimeRange(String[] tickers, Long startTime, Long endTime);

    @Query(value = "{'$group': {_id: null, maxFieldValue: {$max: '$t'}}}")
    Flux<Long> getMaximumTimestamp();

    @Query(value = "{'$group': {_id: null, maxFieldValue: {$min: '$t'}}}")
    Flux<Long> getMinimumTimestamp();
}
