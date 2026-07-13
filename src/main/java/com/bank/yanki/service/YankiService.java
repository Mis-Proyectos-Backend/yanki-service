package com.bank.yanki.service;

import com.bank.yanki.dto.AssociateDebitCardRequest;
import com.bank.yanki.dto.TransferRequest;
import com.bank.yanki.dto.YankiRequest;
import com.bank.yanki.dto.YankiTransferRequest;
import com.bank.yanki.model.YankiWallet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface YankiService {

    /**
     * Register a new Yanki wallet.
     */
    Mono<YankiWallet> create(YankiRequest request);

    /**
     * Get all wallets.
     */
    Flux<YankiWallet> findAll();

    /**
     * Find wallet by id.
     */
    Mono<YankiWallet> findById(String id);

    /**
     * Find wallet by phone number.
     */
    Mono<YankiWallet> findByPhone(String phoneNumber);

    /**
     * Associate a debit card.
     */
    Mono<YankiWallet> associateDebitCard(
            AssociateDebitCardRequest request);

    /**
     * Transfer money between wallets.
     */
    Mono<Void> transfer(
            YankiTransferRequest request);

    /**
     * Delete wallet.
     */
    Mono<Void> delete(String id);

}
