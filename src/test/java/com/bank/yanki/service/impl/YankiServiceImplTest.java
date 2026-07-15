package com.bank.yanki.service.impl;

import com.bank.yanki.client.DebitCardClient;
import com.bank.yanki.client.dto.DebitCardResponse;
import com.bank.yanki.dto.AssociateDebitCardRequest;
import com.bank.yanki.dto.YankiRequest;
import com.bank.yanki.dto.YankiTransferRequest;
import com.bank.yanki.enums.DocumentType;
import com.bank.yanki.enums.PaymentMethod;
import com.bank.yanki.event.AccountTransferEvent;
import com.bank.yanki.kafka.producer.AccountTransferProducer;
import com.bank.yanki.model.YankiWallet;
import com.bank.yanki.repository.YankiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class YankiServiceImplTest {

    private YankiRepository repository;
    private AccountTransferProducer producer;
    private DebitCardClient debitCardClient;
    private YankiServiceImpl service;

    @BeforeEach
    void setUp() {

        repository = mock(YankiRepository.class);

        producer = mock(AccountTransferProducer.class);

        debitCardClient = mock(DebitCardClient.class);

        service = new YankiServiceImpl(
                repository,
                debitCardClient,
                producer
        );
    }

    @Test
    void create_shouldSaveWallet() {

        YankiRequest request =
                YankiRequest.builder()
                        .documentType(DocumentType.DNI)
                        .documentNumber("12345678")
                        .phoneNumber("999111222")
                        .imei("IMEI001")
                        .email("test@test.com")
                        .build();

        YankiWallet wallet =
                YankiWallet.builder()
                        .documentType(DocumentType.DNI)
                        .documentNumber("12345678")
                        .phoneNumber("999111222")
                        .imei("IMEI001")
                        .email("test@test.com")
                        .build();

        when(repository.existsByPhoneNumber("999111222"))
                .thenReturn(Mono.just(false));

        when(repository.save(any(YankiWallet.class)))
                .thenReturn(Mono.just(wallet));

        StepVerifier.create(service.create(request))
                .expectNext(wallet)
                .verifyComplete();

        verify(repository).save(any(YankiWallet.class));
    }

    @Test
    void create_shouldFailWhenPhoneAlreadyExists() {

        YankiRequest request =
                YankiRequest.builder()
                        .phoneNumber("999111222")
                        .build();

        when(repository.existsByPhoneNumber("999111222"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(service.create(request))
                .expectErrorMatches(error ->
                        error instanceof ResponseStatusException ex &&
                                ex.getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                ex.getReason().equals("Phone number already exists"))
                .verify();

        verify(repository, never()).save(any());
    }

    @Test
    void findAll_shouldReturnWallets() {

        YankiWallet wallet =
                YankiWallet.builder()
                        .phoneNumber("999111222")
                        .build();

        when(repository.findAll())
                .thenReturn(Flux.just(wallet));

        StepVerifier.create(service.findAll())
                .expectNext(wallet)
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnWallet() {

        YankiWallet wallet =
                YankiWallet.builder()
                        .id("1")
                        .build();

        when(repository.findById("1"))
                .thenReturn(Mono.just(wallet));

        StepVerifier.create(service.findById("1"))
                .expectNext(wallet)
                .verifyComplete();
    }

    @Test
    void findByPhone_shouldReturnWallet() {

        YankiWallet wallet =
                YankiWallet.builder()
                        .phoneNumber("999111222")
                        .build();

        when(repository.findByPhoneNumber("999111222"))
                .thenReturn(Mono.just(wallet));

        StepVerifier.create(service.findByPhone("999111222"))
                .expectNext(wallet)
                .verifyComplete();
    }

    @Test
    void associateDebitCard_shouldAssociateCard() {

        AssociateDebitCardRequest request =
                AssociateDebitCardRequest.builder()
                        .phoneNumber("999111222")
                        .debitCardId("card001")
                        .build();

        YankiWallet wallet =
                YankiWallet.builder()
                        .phoneNumber("999111222")
                        .build();

        when(repository.findByPhoneNumber("999111222"))
                .thenReturn(Mono.just(wallet));

        when(repository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.associateDebitCard(request))
                .assertNext(result ->
                        assertEquals("card001", result.getDebitCardId()))
                .verifyComplete();
    }

    @Test
    void associateDebitCard_shouldFailWhenWalletNotFound() {

        AssociateDebitCardRequest request =
                AssociateDebitCardRequest.builder()
                        .phoneNumber("999111222")
                        .debitCardId("card001")
                        .build();

        when(repository.findByPhoneNumber("999111222"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.associateDebitCard(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void transfer_shouldPublishKafkaEvent() {

        YankiTransferRequest request =
                YankiTransferRequest.builder()
                        .originPhone("999111222")
                        .destinationPhone("999333444")
                        .amount(BigDecimal.valueOf(100))
                        .description("Payment")
                        .build();

        YankiWallet sender =
                YankiWallet.builder()
                        .phoneNumber("999111222")
                        .debitCardId("card001")
                        .build();

        YankiWallet receiver =
                YankiWallet.builder()
                        .phoneNumber("999333444")
                        .debitCardId("card002")
                        .build();

        DebitCardResponse senderCard = DebitCardResponse.builder()
                .id("card001")
                .primaryAccountId("account001")
                .build();

        DebitCardResponse receiverCard = DebitCardResponse.builder()
                .id("card002")
                .primaryAccountId("account002")
                .build();
        receiverCard.setId("card002");
        receiverCard.setPrimaryAccountId("account002");

        when(repository.findByPhoneNumber("999111222"))
                .thenReturn(Mono.just(sender));

        when(repository.findByPhoneNumber("999333444"))
                .thenReturn(Mono.just(receiver));

        when(debitCardClient.findById("card001"))
                .thenReturn(Mono.just(senderCard));

        when(debitCardClient.findById("card002"))
                .thenReturn(Mono.just(receiverCard));

        when(producer.sendTransfer(any(AccountTransferEvent.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.transfer(request))
                .verifyComplete();

        ArgumentCaptor<AccountTransferEvent> captor =
                ArgumentCaptor.forClass(AccountTransferEvent.class);

        verify(producer).sendTransfer(captor.capture());

        AccountTransferEvent event = captor.getValue();

        assertEquals("account001", event.getSourceAccountId());
        assertEquals("account002", event.getDestinationAccountId());
        assertEquals(BigDecimal.valueOf(100), event.getAmount());
        assertEquals("Payment", event.getDescription());
        assertEquals(PaymentMethod.YANKI, event.getPaymentMethod());
    }

    @Test
    void transfer_shouldFailWhenSenderNotFound() {

        YankiTransferRequest request =
                YankiTransferRequest.builder()
                        .originPhone("999111222")
                        .destinationPhone("999333444")
                        .build();

        when(repository.findByPhoneNumber("999111222"))
                .thenReturn(Mono.empty());

        // Mono.zip() también crea el Mono del destinatario
        when(repository.findByPhoneNumber("999333444"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.transfer(request))
                .expectErrorMatches(error ->
                        error instanceof ResponseStatusException &&
                                ((ResponseStatusException) error).getStatusCode() == HttpStatus.NOT_FOUND &&
                                error.getMessage().contains("Sender wallet not found")
                )
                .verify();

        verify(producer, never()).sendTransfer(any(AccountTransferEvent.class));
        verifyNoInteractions(debitCardClient);
    }

    @Test
    void transfer_shouldFailWhenSenderHasNoDebitCard() {

        YankiTransferRequest request =
                YankiTransferRequest.builder()
                        .originPhone("999111222")
                        .destinationPhone("999333444")
                        .build();

        YankiWallet sender =
                YankiWallet.builder()
                        .phoneNumber("999111222")
                        .build();

        when(repository.findByPhoneNumber("999111222"))
                .thenReturn(Mono.empty());

        // Mono.zip() también crea el Mono del destinatario
        when(repository.findByPhoneNumber("999333444"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.transfer(request))
                .expectError(RuntimeException.class)
                .verify();

        verify(producer, never()).sendTransfer(any());
    }

    @Test
    void transfer_shouldFailWhenReceiverNotFound() {

        YankiTransferRequest request =
                YankiTransferRequest.builder()
                        .originPhone("999111222")
                        .destinationPhone("999333444")
                        .amount(BigDecimal.TEN)
                        .build();

        YankiWallet sender =
                YankiWallet.builder()
                        .phoneNumber("999111222")
                        .debitCardId("card001")
                        .build();

        when(repository.findByPhoneNumber("999111222"))
                .thenReturn(Mono.just(sender));

        when(repository.findByPhoneNumber("999333444"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.transfer(request))
                .expectError(RuntimeException.class)
                .verify();

        verify(producer, never()).sendTransfer(any());
    }

    @Test
    void delete_shouldDeleteWallet() {

        when(repository.deleteById("1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.delete("1"))
                .verifyComplete();

        verify(repository).deleteById("1");
    }

    @Test
    void transfer_shouldFailWhenWalletHasNoDebitCard() {

        YankiTransferRequest request =
                YankiTransferRequest.builder()
                        .originPhone("999111222")
                        .destinationPhone("999333444")
                        .amount(BigDecimal.valueOf(100))
                        .description("Payment")
                        .build();


        YankiWallet sender =
                YankiWallet.builder()
                        .phoneNumber("999111222")
                        .debitCardId(null)
                        .build();


        when(repository.findByPhoneNumber("999111222"))
                .thenReturn(Mono.just(sender));


        // IMPORTANTE: Mono.zip también evalúa el receiver
        when(repository.findByPhoneNumber("999333444"))
                .thenReturn(Mono.empty());


        StepVerifier.create(service.transfer(request))
                .expectErrorMatches(error ->
                        error instanceof ResponseStatusException &&
                                ((ResponseStatusException) error)
                                        .getStatusCode()
                                        .equals(HttpStatus.BAD_REQUEST) &&
                                error.getMessage()
                                        .contains("Sender has no debit card")
                )
                .verify();


        verify(producer, never())
                .sendTransfer(any(AccountTransferEvent.class));


        verifyNoInteractions(debitCardClient);

    }
}
