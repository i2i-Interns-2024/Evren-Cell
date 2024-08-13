package com.i2i.evrencell.aom.request;

import lombok.Builder;

@Builder
public record LoginCustomerRequest(
        String  msisdn,
        String  password
) {
}
