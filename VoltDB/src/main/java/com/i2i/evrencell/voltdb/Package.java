package com.i2i.evrencell.voltdb;

public class Package {
    private Integer packageId;
    private String packageName;
    private double price;
    private Integer amountMinutes;
    private Integer amountData;
    private Integer amountSms;
    private Integer period;

    public Package(Integer packageId, String packageName, double price, Integer amountMinutes, Integer amountData, Integer amountSms, Integer period) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.price = price;
        this.amountMinutes = amountMinutes;
        this.amountData = amountData;
        this.amountSms = amountSms;
        this.period = period;
    }

    public Package(){}

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getAmountMinutes() {
        return amountMinutes;
    }

    public void setAmountMinutes(Integer amountMinutes) {
        this.amountMinutes = amountMinutes;
    }

    public Integer getAmountData() {
        return amountData;
    }

    public void setAmountData(Integer amountData) {
        this.amountData = amountData;
    }

    public Integer getAmountSms() {
        return amountSms;
    }

    public void setAmountSms(Integer amountSms) {
        this.amountSms = amountSms;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

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
