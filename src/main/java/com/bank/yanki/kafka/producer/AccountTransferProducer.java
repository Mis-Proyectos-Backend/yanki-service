package com.bank.yanki.kafka.producer;

import com.bank.yanki.event.AccountTransferEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountTransferProducer {

    private static final String TOPIC = "account-transfer-topic";

    private final KafkaTemplate<String, AccountTransferEvent> kafkaTemplate;

    public Mono<Void> sendTransfer(AccountTransferEvent event) {

        return Mono.fromFuture(
                kafkaTemplate.send(
                        TOPIC,
                        event.getSourceAccountId(),
                        event
                )
        ).then();
    }
}