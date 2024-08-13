package com.i2i.evrencell.aom.request;

import lombok.Builder;

@Builder
public record ForgetPasswordRequest(
        String  email,
        String  TCNumber
) {
}
