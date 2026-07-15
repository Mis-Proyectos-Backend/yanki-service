package com.bank.yanki.client;

import com.bank.yanki.client.dto.DebitCardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebitCardClientTest {


    @Mock
    private WebClient.Builder builder;


    @Mock
    private WebClient webClient;


    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;


    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;


    @Mock
    private WebClient.ResponseSpec responseSpec;


    private DebitCardClient debitCardClient;



    @BeforeEach
    void setUp() {

        debitCardClient = new DebitCardClient(builder);

    }



    @Test
    void findById_shouldReturnDebitCard() {


        DebitCardResponse card =
                DebitCardResponse.builder()
                        .id("card001")
                        .cardNumber("1234567890123456")
                        .primaryAccountId("account001")
                        .customerId("customer001")
                        .build();

        when(builder.build())
                .thenReturn(webClient);
        doReturn(requestHeadersUriSpec)
                .when(webClient)
                .get();


        doReturn(requestHeadersSpec)
                .when(requestHeadersUriSpec)
                .uri(
                        eq("http://debit-card-service/debit-cards/{id}"),
                        eq("card001")
                );


        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);


        when(responseSpec.bodyToMono(DebitCardResponse.class))
                .thenReturn(Mono.just(card));


        StepVerifier.create(
                        debitCardClient.findById("card001")
                )
                .expectNext(card)
                .verifyComplete();


    }


    @Test
    void findById_shouldReturnEmptyWhenServiceFails() {

        when(builder.build())
                .thenReturn(webClient);
        doReturn(requestHeadersUriSpec)
                .when(webClient)
                .get();


        doReturn(requestHeadersSpec)
                .when(requestHeadersUriSpec)
                .uri(
                        eq("http://debit-card-service/debit-cards/{id}"),
                        eq("card001")
                );

        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);


        when(responseSpec.bodyToMono(DebitCardResponse.class))
                .thenReturn(
                        Mono.error(
                                new RuntimeException("service error")
                        )
                );


        StepVerifier.create(
                        debitCardClient.findById("card001")
                )
                .expectError(RuntimeException.class)
                .verify();


    }


    @Test
    void fallbackDebitCard_shouldReturnEmpty() {


        StepVerifier.create(
                        debitCardClient.fallbackDebitCard(
                                "card001",
                                new RuntimeException("error")
                        )
                )
                .verifyComplete();


    }

}