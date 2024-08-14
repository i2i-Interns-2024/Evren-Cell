package com.i2i.evrencell.aom.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ForgetPasswordRequest(
        @NotNull(message = "Email cannot be null")
        @NotBlank(message = "Email cannot be blank")
        @Size(min = 1, max = 255, message = "Email must be between 1 and 255 characters")
        String  email,
        @NotNull(message = "TCNumber cannot be null")
        @NotBlank(message = "TCNumber cannot be blank")
        @Size(min = 11, max = 11, message = "TCNumber must be 11 characters")
        @Pattern(regexp = "\\d{11}", message = "TCNumber must contain only digits")
        String  TCNumber
) {
}
