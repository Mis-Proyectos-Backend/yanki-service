package com.bank.yanki.kafka.producer;

import com.bank.yanki.event.YankiPaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class YankiPaymentProducer {


    private final KafkaTemplate<String, YankiPaymentEvent> kafkaTemplate;


    private static final String TOPIC =
            "yanki-payment-topic";


    public void sendPayment(
            YankiPaymentEvent event) {


        kafkaTemplate.send(
                TOPIC,
                event.getDebitCardId(),
                event
        );

    }

}
