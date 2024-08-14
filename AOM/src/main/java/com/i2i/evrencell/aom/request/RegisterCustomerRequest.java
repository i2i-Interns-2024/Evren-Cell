package com.i2i.evrencell.aom.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Builder
public record RegisterCustomerRequest (

        @NotBlank(message = "Name cannot be blank")
        @NotNull(message = "Name cannot be null")
        @Pattern(regexp = "^[a-zA-Z çşğıüöÇŞĞÜÖİ]+$", message = "Name must contain only alphabetic characters, spaces, and Turkish characters")
        String  name,

        @NotBlank(message = "Surname cannot be blank")
        @NotNull(message = "Surname cannot be null")
        @Pattern(regexp = "^[a-zA-Z çşğıüöÇŞĞÜÖİ]+$", message = "Name must contain only alphabetic characters, spaces, and Turkish characters")
        String  surname,

        @NotBlank(message = "Msisdn cannot be blank")
        @NotNull(message = "Msisdn cannot be null")
        @Size(min = 12, max = 12, message = "Msisdn must be 12 characters")
        @Pattern(regexp = "\\d{12}", message = "Msisdn must contain only digits")
        String  msisdn,

        @NotBlank(message = "Email cannot be blank")
        @NotNull(message = "Email cannot be null")
        @Pattern(regexp = "^[\\w.%+-]+@(gmail\\.com|hotmail\\.com|outlook\\.com|yahoo\\.com|live\\.com)$",
                message = "Email must be a valid address from gmail.com, hotmail.com, outlook.com, yahoo.com, or live.com")
        String  email,

        @NotBlank(message = "Password cannot be blank")
        @NotNull(message = "Password cannot be null")
        String  password,

        @NotBlank(message = "TCNumber cannot be blank")
        @NotNull(message = "TCNumber cannot be null")
        @Size(min = 11, max = 11, message = "TCNumber must be 11 characters")
        @Pattern(regexp = "\\d{11}", message = "TCNumber must contain only digits")
        String  TCNumber,

        @NotBlank(message = "PackageName cannot be blank")
        @NotNull(message = "PackageName cannot be null")
        String  packageName
){ }
