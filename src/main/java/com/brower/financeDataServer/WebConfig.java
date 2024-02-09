package com.brower.financeDataServer;

import com.brower.financeDataServer.data.aggregate.AggregatesWebSocketHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import javax.net.ssl.SSLException;
import javax.net.ssl.X509ExtendedKeyManager;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
        return http.authorizeExchange(exchanges -> exchanges.anyExchange().permitAll()).build();
    }

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() throws CertificateException, SSLException {
        SslContext sslCtx = SslContextBuilder.forServer(new File("cert.pem"), new File("key.pem")).build();

        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(server -> server.secure(sslContextSpec -> sslContextSpec.sslContext(sslCtx)));
        return factory;
    }
}
