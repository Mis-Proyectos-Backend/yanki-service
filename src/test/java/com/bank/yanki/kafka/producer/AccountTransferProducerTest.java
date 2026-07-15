package com.bank.yanki.kafka.producer;

import com.bank.yanki.enums.PaymentMethod;
import com.bank.yanki.event.AccountTransferEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class AccountTransferProducerTest {

    private KafkaTemplate<String, AccountTransferEvent> kafkaTemplate;

    private AccountTransferProducer producer;

    @BeforeEach
    void setUp() {

        kafkaTemplate = mock(KafkaTemplate.class);

        producer = new AccountTransferProducer(kafkaTemplate);

    }

    @Test
    void sendPayment_shouldSendMessageToKafkaTopic() {

        AccountTransferEvent event =
                AccountTransferEvent.builder()
                        .sourceAccountId("account001")
                        .destinationAccountId("account002")
                        .amount(BigDecimal.valueOf(100))
                        .paymentMethod(PaymentMethod.YANKI)
                        .description("Yanki payment")
                        .date(LocalDateTime.now())
                        .build();

        CompletableFuture<SendResult<String, AccountTransferEvent>> future =
                CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(anyString(), anyString(), any(AccountTransferEvent.class))).thenReturn(future);
        StepVerifier.create(producer.sendTransfer(event))
                .verifyComplete();

        verify(kafkaTemplate).send(
                "account-transfer-topic",
                "account001",
                event
        );

    }

}