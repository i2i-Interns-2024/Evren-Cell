package com.i2i.evrencell.voltdb;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record VoltCustomerBalance(
        String msisdn,
        int balanceData,
        int balanceSms,
        int balanceMinutes,
        Timestamp sdate,
        Timestamp edate
) {
}
