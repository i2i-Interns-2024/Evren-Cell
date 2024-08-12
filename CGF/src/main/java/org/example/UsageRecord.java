package org.example;

import oracle.sql.TIMESTAMP;

import java.sql.Timestamp;
import java.util.Date;

public class UsageRecord {
    private String msisdn1;
    private String msisdn2;
    private String usageType;
    private Integer usageDuration;
    private Timestamp usageDate;

    public UsageRecord(String msisdn1, String msisdn2, String usageType, Integer usageDuration, Timestamp usageDate) {
        this.msisdn1 = msisdn1;
        this.msisdn2 = msisdn2;
        this.usageType = usageType;
        this.usageDuration = usageDuration;
        this.usageDate = usageDate;
    }

    public UsageRecord() {
    }

    public String getMsisdn1() {
        return msisdn1;
    }

    public void setMsisdn1(String msisdn1) {
        this.msisdn1 = msisdn1;
    }

    public String getMsisdn2() {
        return msisdn2;
    }

    public void setMsisdn2(String msisdn2) {
        this.msisdn2 = msisdn2;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public Integer getUsageDuration() {
        return usageDuration;
    }

    public void setUsageDuration(Integer usageDuration) {
        this.usageDuration = usageDuration;
    }

    public Timestamp getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(Timestamp usageDate) {
        this.usageDate = usageDate;
    }
}
