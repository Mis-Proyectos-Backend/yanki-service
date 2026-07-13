package com.bank.yanki.service.impl;

import com.bank.yanki.dto.AssociateDebitCardRequest;
import com.bank.yanki.dto.YankiRequest;
import com.bank.yanki.dto.YankiTransferRequest;
import com.bank.yanki.event.YankiPaymentEvent;
import com.bank.yanki.kafka.producer.YankiPaymentProducer;
import com.bank.yanki.model.YankiWallet;
import com.bank.yanki.repository.YankiRepository;
import com.bank.yanki.service.YankiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class YankiServiceImpl implements YankiService {

    private final YankiRepository repository;
    private final YankiPaymentProducer producer;

    @Override
    public Mono<YankiWallet> create(YankiRequest request) {

        return repository.existsByPhoneNumber(request.getPhoneNumber())
                .flatMap(exists -> {

                    if (exists) {
                        return Mono.error(
                                new RuntimeException("Phone number already exists"));
                    }

                    YankiWallet wallet =
                            YankiWallet.builder()
                                    .documentType(request.getDocumentType())
                                    .documentNumber(request.getDocumentNumber())
                                    .phoneNumber(request.getPhoneNumber())
                                    .imei(request.getImei())
                                    .email(request.getEmail())
                                    .build();

                    return repository.save(wallet);

                });

    }

    @Override
    public Flux<YankiWallet> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<YankiWallet> findById(String id) {
        return repository.findById(id);

    }

    @Override
    public Mono<YankiWallet> findByPhone(String phoneNumber) {

        return repository.findByPhoneNumber(phoneNumber);

    }


    @Override
    public Mono<YankiWallet> associateDebitCard(AssociateDebitCardRequest request) {

        return repository.findByPhoneNumber(request.getPhoneNumber())
                .switchIfEmpty(
                        Mono.error(
                                new RuntimeException(
                                        "Wallet not found"
                                )
                        )
                )
                .flatMap(wallet -> {
                    wallet.setDebitCardId(request.getDebitCardId());
                    return repository.save(wallet);

                });

    }

    @Override
    public Mono<Void> transfer(YankiTransferRequest request) {
        return repository.findByPhoneNumber(request.getOriginPhone())
                .switchIfEmpty(
                        Mono.error(new RuntimeException("Sender wallet not found")))
                .flatMap(sender -> {
                    if(sender.getDebitCardId() == null){
                        return Mono.error(new RuntimeException("Sender has no debit card"));
                    }

                    return repository.findByPhoneNumber(request.getDestinationPhone())
                            .switchIfEmpty(
                                    Mono.error(new RuntimeException("Receiver wallet not found")))
                            .then(Mono.fromRunnable(() -> {
                                YankiPaymentEvent event = YankiPaymentEvent.builder()
                                                .debitCardId(sender.getDebitCardId())
                                                .destinationPhone(request.getDestinationPhone())
                                                .amount(request.getAmount())
                                                .description(request.getDescription())
                                                .date(LocalDateTime.now())
                                                .build();
                                producer.sendPayment(event);
                            }));
                });
    }

    @Override
    public Mono<Void> delete(String id) {

        return repository.deleteById(id);

    }
}
