package com.brower.financeDataServer.data.user;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FinanceUserRepository extends ReactiveMongoRepository<FinanceUser, String> {
    Mono<FinanceUser> findByEmail(String email);
}
