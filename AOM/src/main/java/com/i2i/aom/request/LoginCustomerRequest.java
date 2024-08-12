package com.i2i.aom.request;

import lombok.Builder;

@Builder
public record LoginCustomerRequest(
        String  msisdn,
        String  password
) {
}
