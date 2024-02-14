package com.brower.financeDataServer.data.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    @Autowired
    private FinanceUserRepository repository;

    public Mono<FinanceUser> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Flux<FinanceUser> findAllUsers() {
        return repository.findAll();
    }

    public void createUser(String email, String password) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        repository
                .findByEmail(email)
                .switchIfEmpty(
                        repository.insert(FinanceUser
                                .builder()
                                .email(email)
                                .password(passwordEncoder.encode(password))
                                .build())
                        )
                .map(user -> Mono.error(new RuntimeException("User already exists")))
                .subscribe();
    }
}
