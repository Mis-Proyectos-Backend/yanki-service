package com.bank.yanki.kafka.producer;

import com.bank.yanki.event.YankiPaymentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class YankiPaymentProducerTest {

    private KafkaTemplate<String, YankiPaymentEvent> kafkaTemplate;

    private YankiPaymentProducer producer;

    @BeforeEach
    void setUp() {

        kafkaTemplate = mock(KafkaTemplate.class);

        producer = new YankiPaymentProducer(kafkaTemplate);

    }

    @Test
    void sendPayment_shouldSendMessageToKafkaTopic() {

        YankiPaymentEvent event =
                YankiPaymentEvent.builder()
                        .debitCardId("card001")
                        .destinationPhone("999888777")
                        .amount(BigDecimal.valueOf(100))
                        .description("Yanki payment")
                        .date(LocalDateTime.now())
                        .build();

        producer.sendPayment(event);

        verify(kafkaTemplate).send(
                "yanki-payment-topic",
                "card001",
                event
        );

    }

}