package com.bank.yanki.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published by Yanki Service to request
 * a payment using an associated debit card.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YankiPaymentEvent {

    private String debitCardId;

    private String destinationPhone;

    private BigDecimal amount;

    private String description;

    private LocalDateTime date;
}
