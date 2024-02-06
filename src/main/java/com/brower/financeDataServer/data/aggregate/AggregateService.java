package com.brower.financeDataServer.data.aggregate;

//import io.reactivex.rxjava3.core.Flowable;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@NoArgsConstructor
public class AggregateService {
    @Autowired
    private AggregatesRepository repository;

    public Flux<Aggregate> getTickersInTimeRange(List<String> tickers, Long startTime, Long endTime) {
        return repository.findTickersInTimeRange(tickers.toArray(String[]::new), startTime, endTime);
    }

    public Flux<Aggregate> getDataTest() {
//        return Observable.just(new Aggregate());
//        return repository.findAll(Example.of(Aggregate.builder().ticker("A").build()));
//        System.out.println(repository);
//        return repository.findAll();
        return repository.findByTicker("CA");
    }
}
