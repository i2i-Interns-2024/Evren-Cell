package com.i2i.evrencell.kafka.message;



import java.sql.Timestamp;

public class UsageRecordMessage implements Message {
    private String callerMsisdn;
    private String calleeMsisdn;
    private BalanceType usageType;
    private Integer usageDuration;
    private Timestamp usageDate;

    public UsageRecordMessage(String callerMsisdn, String calleeMsisdn, BalanceType usageType, Integer usageDuration, Timestamp usageDate) {
        this.callerMsisdn = callerMsisdn;
        this.calleeMsisdn = calleeMsisdn;
        this.usageType = usageType;
        this.usageDuration = usageDuration;
        this.usageDate = usageDate;
    }

    public UsageRecordMessage() {
    }

    public String getCallerMsisdn() {
        return callerMsisdn;
    }

    public void setCallerMsisdn(String callerMsisdn) {
        this.callerMsisdn = callerMsisdn;
    }

    public String getCalleeMsisdn() {
        return calleeMsisdn;
    }

    public void setCalleeMsisdn(String calleeMsisdn) {
        this.calleeMsisdn = calleeMsisdn;
    }

    public BalanceType getUsageType() {
        return usageType;
    }

    public void setUsageType(BalanceType usageType) {
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