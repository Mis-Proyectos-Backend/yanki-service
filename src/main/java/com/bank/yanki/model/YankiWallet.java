package com.bank.yanki.model;

import com.bank.yanki.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wallets")
public class YankiWallet {


    @Id
    private String id;

    /**
     * Cliente
     */
    private String customerId;

    /**
     * Tipo de documento:
     * DNI, CEX, PASSPORT
     */
    private DocumentType documentType;


    /**
     * Número de documento del usuario
     */
    private String documentNumber;


    /**
     * Número celular único del monedero
     */
    @Indexed(unique = true)
    private String phoneNumber;


    /**
     * Identificador del dispositivo móvil
     */
    @Indexed(unique = true)
    private String imei;


    /**
     * Correo del usuario
     */
    private String email;


    /**
     * Associated debit card identifier.
     */
    private String debitCardId;


    /**
     * Fecha de creación
     */
    private LocalDateTime createdAt;

}

