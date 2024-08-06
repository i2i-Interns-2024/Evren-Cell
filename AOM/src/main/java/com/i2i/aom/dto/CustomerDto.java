package com.i2i.aom.dto;

import java.util.Date;

public record CustomerDto(
    Integer customerId,
    String msisdn,
    String name,
    String surname,
    String email,
    String status,
    String securityKey,
    Date sDate
) { }
