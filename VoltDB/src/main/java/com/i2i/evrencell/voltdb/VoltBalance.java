package com.i2i.evrencell.voltdb;

import lombok.*;

import java.util.Date;

@Setter
@Getter
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
