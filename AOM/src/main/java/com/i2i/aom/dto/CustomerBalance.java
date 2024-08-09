package com.i2i.aom.dto;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record CustomerBalance(
        String msisdn,
        int balanceData,
        int balanceSms,
        int balanceMinutes,
        Timestamp sdate,
        Timestamp edate
) {
}
