package com.i2i.evrencell.aom.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginCustomerRequest(
        @NotNull(message = "MSISDN cannot be null")
        @NotBlank(message = "MSISDN cannot be blank")
        @Size(min = 12, max = 12, message = "MSISDN must be 10 characters")
        String  msisdn,
        @NotNull(message = "Password cannot be null")
        @NotBlank(message = "Password cannot be blank")
        String  password
) {
}
