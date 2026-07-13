package com.bank.yanki.dto;

import com.bank.yanki.enums.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class YankiRequest {

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Document number is required")
    private String documentNumber;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "IMEI is required")
    private String imei;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

}
