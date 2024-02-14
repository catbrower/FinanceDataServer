package com.brower.financeDataServer.data.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Mono<FinanceUser> getUserById(@PathVariable String id) {
        return Mono.just(new FinanceUser());
    }

    @GetMapping("/")
    public Flux<FinanceUser> getAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/create")
    public String createUser(@RequestBody CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest.getEmail(), createUserRequest.getPassword());
        return "OK";
    }
}
