package com.bank.yanki.client.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data

public class DebitCardResponse {

    private String id;

    private String cardNumber;

    private String primaryAccountId;

    private String customerId;

}
