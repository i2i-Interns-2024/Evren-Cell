package com.i2i.evrencell.voltdb;

import lombok.Builder;

import java.util.Date;

@Builder
public class VoltBalance {
    private Integer balanceId;
    private Integer packageId;
    private Integer customerId;
    private Integer partitionId;
    private Integer balanceLevelMinutes;
    private Integer balanceLevelSMS;
    private Integer balanceLevelData;
    private Integer balanceLevelMoney;
    private Date    sDate;
    private Date    eDate;
    private long    price;

    public VoltBalance(Integer  balanceId,
                       Integer  packageId,
                       Integer  customerId,
                       Integer  partitionId,
                       Integer  balanceLevelMinutes,
                       Integer  balanceLevelSMS,
                       Integer  balanceLevelData,
                       Integer  balanceLevelMoney,
                       Date     sDate,
                       Date     eDate,
                       long     price) {
        this.balanceId = balanceId;
        this.packageId = packageId;
        this.customerId = customerId;
        this.partitionId = partitionId;
        this.balanceLevelMinutes = balanceLevelMinutes;
        this.balanceLevelSMS = balanceLevelSMS;
        this.balanceLevelData = balanceLevelData;
        this.balanceLevelMoney = balanceLevelMoney;
        this.sDate = sDate;
        this.eDate = eDate;
        this.price = price;
    }

    public VoltBalance() {}

    public Integer getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(Integer balanceId) {
        this.balanceId = balanceId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Integer getBalanceLevelMinutes() {
        return balanceLevelMinutes;
    }

    public void setBalanceLevelMinutes(Integer balanceLevelMinutes) {
        this.balanceLevelMinutes = balanceLevelMinutes;
    }

    public Integer getBalanceLevelSMS() {
        return balanceLevelSMS;
    }

    public void setBalanceLevelSMS(Integer balanceLevelSMS) {
        this.balanceLevelSMS = balanceLevelSMS;
    }

    public Integer getBalanceLevelData() {
        return balanceLevelData;
    }

    public void setBalanceLevelData(Integer balanceLevelData) {
        this.balanceLevelData = balanceLevelData;
    }

    public Integer getBalanceLevelMoney() {
        return balanceLevelMoney;
    }

    public void setBalanceLevelMoney(Integer balanceLevelMoney) {
        this.balanceLevelMoney = balanceLevelMoney;
    }

    public Date getsDate() {
        return sDate;
    }

    public void setsDate(Date sDate) {
        this.sDate = sDate;
    }

    public Date geteDate() {
        return eDate;
    }

    public void seteDate(Date eDate) {
        this.eDate = eDate;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Package{" +
                "balanceId=" + balanceId +
                ", packageId=" + packageId +
                ", customerId=" + customerId +
                ", partitionId=" + partitionId +
                ", balanceLevelMinutes=" + balanceLevelMinutes +
                ", balanceLevelSMS=" + balanceLevelSMS +
                ", balanceLevelData=" + balanceLevelData +
                ", balanceLevelMoney=" + balanceLevelMoney +
                ", sDate=" + sDate +
                ", eDate=" + eDate +
                ", price=" + price +
                '}';
    }
}
