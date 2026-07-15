package com.bank.yanki.controller;

import com.bank.yanki.dto.AssociateDebitCardRequest;
import com.bank.yanki.dto.YankiRequest;
import com.bank.yanki.dto.YankiTransferRequest;
import com.bank.yanki.enums.DocumentType;
import com.bank.yanki.model.YankiWallet;
import com.bank.yanki.service.YankiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(YankiController.class)
class YankiControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private YankiService service;

    @Test
    void create_shouldReturnCreatedWallet() {

        YankiRequest request =
                YankiRequest.builder()
                        .customerId("123456")
                        .documentType(DocumentType.DNI)
                        .documentNumber("12345678")
                        .phoneNumber("999111222")
                        .imei("IMEI001")
                        .email("test@test.com")
                        .build();

        YankiWallet wallet =
                YankiWallet.builder()
                        .id("1")
                        .customerId("123456")
                        .documentType(DocumentType.DNI)
                        .documentNumber("12345678")
                        .phoneNumber("999111222")
                        .imei("IMEI001")
                        .email("test@test.com")
                        .build();

        when(service.create(any()))
                .thenReturn(Mono.just(wallet));

        webTestClient.post()
                .uri("/api/yanki")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");

        verify(service).create(any());
    }

    @Test
    void findAll_shouldReturnWallets() {

        YankiWallet wallet =
                YankiWallet.builder()
                        .id("1")
                        .phoneNumber("999111222")
                        .build();

        when(service.findAll())
                .thenReturn(Flux.just(wallet));

        webTestClient.get()
                .uri("/api/yanki")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1");

        verify(service).findAll();
    }

    @Test
    void findById_shouldReturnWallet() {

        YankiWallet wallet =
                YankiWallet.builder()
                        .id("1")
                        .phoneNumber("999111222")
                        .build();

        when(service.findById("1"))
                .thenReturn(Mono.just(wallet));

        webTestClient.get()
                .uri("/api/yanki/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");

        verify(service).findById("1");
    }

    @Test
    void findByPhone_shouldReturnWallet() {

        YankiWallet wallet =
                YankiWallet.builder()
                        .id("1")
                        .phoneNumber("999111222")
                        .build();

        when(service.findByPhone("999111222"))
                .thenReturn(Mono.just(wallet));

        webTestClient.get()
                .uri("/api/yanki/phone/999111222")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.phoneNumber").isEqualTo("999111222");

        verify(service).findByPhone("999111222");
    }

    @Test
    void associateDebitCard_shouldReturnUpdatedWallet() {

        AssociateDebitCardRequest request =
                AssociateDebitCardRequest.builder()
                        .phoneNumber("999111222")
                        .debitCardId("card001")
                        .build();

        YankiWallet wallet =
                YankiWallet.builder()
                        .phoneNumber("999111222")
                        .debitCardId("card001")
                        .build();

        when(service.associateDebitCard(any()))
                .thenReturn(Mono.just(wallet));

        webTestClient.put()
                .uri("/api/yanki/associate-debit-card")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.debitCardId").isEqualTo("card001");

        verify(service).associateDebitCard(any());
    }

    @Test
    void transfer_shouldReturnOk() {

        YankiTransferRequest request =
                YankiTransferRequest.builder()
                        .originPhone("999111222")
                        .destinationPhone("999333444")
                        .amount(BigDecimal.valueOf(100))
                        .description("Transfer")
                        .build();

        when(service.transfer(any()))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/yanki/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        verify(service).transfer(any());
    }

    @Test
    void delete_shouldReturnNoContent() {

        when(service.delete("1"))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/yanki/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(service).delete("1");
    }

}
