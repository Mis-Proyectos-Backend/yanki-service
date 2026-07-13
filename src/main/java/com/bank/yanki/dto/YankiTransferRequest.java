package com.bank.yanki.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YankiTransferRequest {


    /**
     * Phone number of sender wallet.
     */
    @NotBlank(message = "Origin phone is required")
    private String originPhone;


    /**
     * Phone number of receiver wallet.
     */
    @NotBlank(message = "Destination phone is required")
    private String destinationPhone;


    /**
     * Amount to transfer.
     */
    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than zero"
    )
    private BigDecimal amount;


    /**
     * Transfer description.
     */
    private String description;

}

