package com.i2i.evrencell.aom.request;

import lombok.Builder;

@Builder
public record RegisterCustomerRequest (
        String  name,
        String  surname,
        String  msisdn,
        String  email,
        String  password,
        String  TCNumber,
        String  packageName
){
}
