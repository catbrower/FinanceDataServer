package com.brower.financeDataServer;

import com.brower.financeDataServer.data.aggregate.AggregatesWebSocketHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import javax.net.ssl.SSLException;
import java.io.File;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
public class WebConfig implements WebFluxConfigurer {
    @Autowired
    AggregatesWebSocketHandler aggregatesWebSocketHandler;

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/aggregates", aggregatesWebSocketHandler);

        return new SimpleUrlHandlerMapping(map, -1);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        Basic auth
        http
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

//        http
//                .authorizeExchange((authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().authenticated()))
//                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() throws CertificateException, SSLException {
        SslContext sslCtx = SslContextBuilder.forServer(new File("cert.pem"), new File("key.pem")).build();

        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(server -> server.secure(sslContextSpec -> sslContextSpec.sslContext(sslCtx)));
        return factory;
    }

    // For testing only
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User
                .withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

// Use something like this for prod
//    @Service
//    class UserService(private val customerService: CustomerService) : ReactiveUserDetailsService {
//
//        override fun findByUsername(username: String?): Mono<UserDetails> = mono {
//            val customer: Customer = customerService.findByEmail(username!!)
//            ?: throw BadCredentialsException("Invalid Credentials")
//
//            val authorities: List<GrantedAuthority> = listOf(customer)
//
//            org.springframework.security.core.userdetails.User(
//                    customer.email,
//                    customer.password,
//                    authorities
//            )
//        }
//    }
}
