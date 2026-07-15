package com.bank.yanki.event;

import com.bank.yanki.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferEvent {

    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String description;
    private LocalDateTime date;
}