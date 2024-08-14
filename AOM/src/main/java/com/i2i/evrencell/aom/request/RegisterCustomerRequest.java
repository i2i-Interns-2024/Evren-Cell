package com.i2i.evrencell.aom.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Builder
public record RegisterCustomerRequest (

        @NotBlank(message = "Name cannot be blank")
        @NotNull(message = "Name cannot be null")
        String  name,
        @NotBlank(message = "Surname cannot be blank")
        @NotNull(message = "Surname cannot be null")
        String  surname,
        @NotBlank(message = "Msisdn cannot be blank")
        @NotNull(message = "Msisdn cannot be null")
        @Size(min = 12, max = 12, message = "Msisdn must be 12 characters")
        String  msisdn,
        @NotBlank(message = "Email cannot be blank")
        @NotNull(message = "Email cannot be null")
        String  email,
        @NotBlank(message = "Password cannot be blank")
        @NotNull(message = "Password cannot be null")
        String  password,
        @NotBlank(message = "TCNumber cannot be blank")
        @NotNull(message = "TCNumber cannot be null")
        @Size(min = 11, max = 11, message = "TCNumber must be 11 characters")
        String  TCNumber,
        @NotBlank(message = "PackageName cannot be blank")
        @NotNull(message = "PackageName cannot be null")
        String  packageName
){
}
