package com.brower.financeDataServer;

import com.brower.financeDataServer.data.aggregate.AggregatesWebSocketHandler;
import com.brower.financeDataServer.data.user.UserService;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLException;
import java.io.File;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebConfig implements WebFluxConfigurer {
    @Autowired
    AggregatesWebSocketHandler aggregatesWebSocketHandler;

    @Autowired
    UserService userService;

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/aggregates", aggregatesWebSocketHandler);

        return new SimpleUrlHandlerMapping(map, -1);
    }

    ReactiveAuthenticationManager customAuthenticationManger() {
        return authentication -> {
            String name = authentication.getName();
            String password = authentication.getCredentials().toString();

            return Mono.just(new UsernamePasswordAuthenticationToken(
                    name, password, new ArrayList<>()));
        };
    }

    ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver() {
        return exchange -> {
            return Mono.just(customAuthenticationManger());
        };
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/users/create")
                        .permitAll()
                        .anyExchange()
                        .authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .addFilterAfter(
                        new AuthenticationWebFilter((ReactiveAuthenticationManager) authentication -> {
                            String name = authentication.getName();
                            String password = authentication.getCredentials().toString();

                            return userService.getUserByEmail(name)
                                    .switchIfEmpty(Mono.error(new UsernameNotFoundException("")))
                                    .map(user -> new UsernamePasswordAuthenticationToken(
                                            name, password, new ArrayList<>()));
                        }),
                        SecurityWebFiltersOrder.REACTOR_CONTEXT)
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() throws CertificateException, SSLException {
        SslContext sslCtx = SslContextBuilder.forServer(new File("cert.pem"), new File("key.pem")).build();

        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(server -> server.secure(sslContextSpec -> sslContextSpec.sslContext(sslCtx)));
        return factory;
    }
}
