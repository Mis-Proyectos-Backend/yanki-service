package com.bank.yanki.repository;

import com.bank.yanki.model.YankiWallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface YankiRepository
        extends ReactiveMongoRepository<YankiWallet, String> {

    /**
     * Buscar un monedero por número de celular.
     *
     * @param phoneNumber número celular
     * @return monedero
     */
    Mono<YankiWallet> findByPhoneNumber(String phoneNumber);

    /**
     * Buscar un monedero por IMEI.
     *
     * @param imei identificador del dispositivo
     * @return monedero
     */
    Mono<YankiWallet> findByImei(String imei);

    /**
     * Buscar un monedero por número de documento.
     *
     * @param documentNumber documento del usuario
     * @return monedero
     */
    Mono<YankiWallet> findByDocumentNumber(String documentNumber);

    /**
     * Verificar si existe un celular registrado.
     */
    Mono<Boolean> existsByPhoneNumber(String phoneNumber);

    /**
     * Verificar si existe un IMEI registrado.
     */
    Mono<Boolean> existsByImei(String imei);

}
