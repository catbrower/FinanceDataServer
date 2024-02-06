package com.brower.financeDataServer.data.aggregate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Component
public class AggregatesWebSocketHandler implements WebSocketHandler {
    @Autowired
    private AggregateService aggregateService;

    private static AggregatesRequest buildRequest(WebSocketMessage webSocketMessage) throws IOException {
            return new ObjectMapper()
                    .readValue(webSocketMessage.getPayload().asInputStream().readAllBytes(), AggregatesRequest.class);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> msg0 = aggregateService.getTickersInTimeRange(List.of("A", "CA"), 0L, 0L).map(Aggregate::toString).map(session::textMessage);

        return session.send(
            session.receive()
                .flatMap(webSocketMessage -> {
                    try {
                        AggregatesRequest request = AggregatesWebSocketHandler.buildRequest(webSocketMessage);
                        if(request.getTickers() == null || request.getTickers().isEmpty() || request.getFrom() == null || request.getTo() == null) {
                            return Mono.just("");
                        }

                        Flux<String> headers = Flux.just(String.join(",", AggregateEncoder.HEADERS));
                        Flux<String> terminal = Flux.just("END");
                        Flux<String> results = aggregateService
                                .getTickersInTimeRange(request.getTickers(), request.getFrom(), request.getTo())
                                .map(AggregateEncoder::encode);

                        return headers.concatWith(results).concatWith(terminal);

                    } catch (Exception err) {
                        return Mono.just(err.getMessage());
                    }
                })
                .map(session::textMessage)
        );

    }
}
