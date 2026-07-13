package com.bank.yanki.controller;

import com.bank.yanki.dto.AssociateDebitCardRequest;
import com.bank.yanki.dto.YankiRequest;
import com.bank.yanki.dto.YankiTransferRequest;
import com.bank.yanki.model.YankiWallet;
import com.bank.yanki.service.YankiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for Yanki wallet operations.
 */
@RestController
@RequestMapping("/api/yanki")
@RequiredArgsConstructor
public class YankiController {

    private final YankiService yankiService;

    /**
     * Create a new Yanki wallet.
     *
     * @param request wallet information
     * @return created wallet
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<YankiWallet> create(
            @Valid @RequestBody YankiRequest request) {

        return yankiService.create(request);
    }

    /**
     * Get all wallets.
     *
     * @return list of wallets
     */
    @GetMapping
    public Flux<YankiWallet> findAll() {
        return yankiService.findAll();
    }

    /**
     * Get wallet by id.
     *
     * @param id wallet id
     * @return wallet
     */
    @GetMapping("/{id}")
    public Mono<YankiWallet> findById(
            @PathVariable String id) {

        return yankiService.findById(id);
    }

    /**
     * Find wallet by phone number.
     *
     * @param phone phone number
     * @return wallet
     */
    @GetMapping("/phone/{phone}")
    public Mono<YankiWallet> findByPhone(
            @PathVariable String phone) {

        return yankiService.findByPhone(phone);
    }

    /**
     * Associate a debit card.
     *
     * @param request association request
     * @return updated wallet
     */

    @PutMapping("/associate-debit-card")
    @ResponseStatus(HttpStatus.OK)
    public Mono<YankiWallet> associateDebitCard(
            @Valid
            @RequestBody
            AssociateDebitCardRequest request) {

        return yankiService.associateDebitCard(request);

    }

    /**
     * Transfer money between wallets.
     *
     * @param request transfer request
     * @return completed signal
     */
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> transfer(
            @Valid @RequestBody YankiTransferRequest request) {

        return yankiService.transfer(request);
    }


    /**
     * Delete wallet.
     *
     * @param id wallet id
     * @return completed signal
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(
            @PathVariable String id) {

        return yankiService.delete(id);
    }


}
