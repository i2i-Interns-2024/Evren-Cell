package com.i2i.evrencell.voltdb;

public record VoltPackageDetails(
        int period,
        int amountMinutes,
        int amountSms,
        int amountData
) {
}
