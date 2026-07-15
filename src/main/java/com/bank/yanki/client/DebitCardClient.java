package com.bank.yanki.client;

import com.bank.yanki.client.dto.DebitCardResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DebitCardClient {

    private final WebClient.Builder builder;

    @CircuitBreaker(name = "debitCardService", fallbackMethod = "fallbackDebitCard")
    public Mono<DebitCardResponse> findById(String id) {

        return builder.build()
                .get()
                .uri("http://debit-card-service/debit-cards/{id}", id)
                .retrieve()
                .bodyToMono(DebitCardResponse.class);
    }
    public Mono<DebitCardResponse> fallbackDebitCard(String id, Throwable ex) {
        return Mono.empty();
    }

}

