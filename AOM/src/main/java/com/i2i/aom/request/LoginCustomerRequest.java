package com.i2i.aom.request;

public record LoginCustomerRequest(
        String msisdn,
        String password
) {
}
