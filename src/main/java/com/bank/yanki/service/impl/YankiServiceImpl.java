package com.bank.yanki.service.impl;

import com.bank.yanki.client.DebitCardClient;
import com.bank.yanki.dto.AssociateDebitCardRequest;
import com.bank.yanki.dto.YankiRequest;
import com.bank.yanki.dto.YankiTransferRequest;
import com.bank.yanki.enums.PaymentMethod;
import com.bank.yanki.event.AccountTransferEvent;
import com.bank.yanki.kafka.producer.AccountTransferProducer;
import com.bank.yanki.model.YankiWallet;
import com.bank.yanki.repository.YankiRepository;
import com.bank.yanki.service.YankiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class YankiServiceImpl implements YankiService {

    private final YankiRepository repository;
    private final DebitCardClient debitCardClient;
    private final AccountTransferProducer transferProducer;

    @Override
    public Mono<YankiWallet> create(YankiRequest request) {
        System.out.println("request: " + request.getCustomerId());
        return repository.existsByPhoneNumber(request.getPhoneNumber())
                .flatMap(exists -> {

                    if (exists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Phone number already exists"));
                    }

                    YankiWallet wallet =
                            YankiWallet.builder()
                                    .customerId(request.getCustomerId())
                                    .documentType(request.getDocumentType())
                                    .documentNumber(request.getDocumentNumber())
                                    .phoneNumber(request.getPhoneNumber())
                                    .imei(request.getImei())
                                    .email(request.getEmail())
                                    .createdAt(LocalDateTime.now())
                                    .build();
                    System.out.println("YankiWallet: " + wallet.getCustomerId());
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
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found")))
                .flatMap(wallet -> {
                    wallet.setDebitCardId(request.getDebitCardId());
                    return repository.save(wallet);

                });

    }

    @Override
    public Mono<Void> transfer(YankiTransferRequest request) {

        return Mono.zip(getWalletWithCard(request.getOriginPhone(), "Sender"), getWalletWithCard(request.getDestinationPhone(), "Receiver"))
                .flatMap(wallets ->
                        buildTransferEvent(wallets.getT1(), wallets.getT2(), request))
                .flatMap(transferProducer::sendTransfer);
    }
    private Mono<YankiWallet> getWalletWithCard(String phone, String owner) {
        return repository.findByPhoneNumber(phone)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, owner + " wallet not found")))
                .flatMap(wallet -> {
                    if (wallet.getDebitCardId() == null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, owner + " has no debit card"));
                    }
                    return Mono.just(wallet);
                });
    }

    private Mono<AccountTransferEvent> buildTransferEvent(YankiWallet sender, YankiWallet receiver, YankiTransferRequest request) {

        return Mono.zip(debitCardClient.findById(sender.getDebitCardId()), debitCardClient.findById(receiver.getDebitCardId()))
                .map(cards -> AccountTransferEvent.builder().sourceAccountId(cards.getT1().getPrimaryAccountId())
                        .destinationAccountId(cards.getT2().getPrimaryAccountId())
                        .amount(request.getAmount())
                        .paymentMethod(PaymentMethod.YANKI)
                        .description(request.getDescription())
                        .date(LocalDateTime.now())
                        .build()
        );

    }

    @Override
    public Mono<Void> delete(String id) {

        return repository.deleteById(id);

    }
}
