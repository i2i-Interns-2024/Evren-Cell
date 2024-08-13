package com.i2i.evrencell.voltdb;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class VoltPackage {
    private Integer packageId;
    private String packageName;
    private double price;
    private Integer amountMinutes;
    private Integer amountData;
    private Integer amountSms;
    private Integer period;

    public VoltPackage(Integer packageId, String packageName, double price, Integer amountMinutes, Integer amountData, Integer amountSms, Integer period) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.price = price;
        this.amountMinutes = amountMinutes;
        this.amountData = amountData;
        this.amountSms = amountSms;
        this.period = period;
    }

    public VoltPackage(){}


    @Override
    public String toString() {
        return "Package{" +
                "packageId=" + packageId +
                ", packageName='" + packageName + '\'' +
                ", price=" + price +
                ", amountMinutes=" + amountMinutes +
                ", amountData=" + amountData +
                ", amountSms=" + amountSms +
                ", period=" + period +
                '}';
    }
}
