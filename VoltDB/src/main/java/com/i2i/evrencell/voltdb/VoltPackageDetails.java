package com.i2i.evrencell.voltdb;


import lombok.Builder;

@Builder
public record VoltPackageDetails(
        int period,
        int amountMinutes,
        int amountSms,
        int amountData
) {
}
