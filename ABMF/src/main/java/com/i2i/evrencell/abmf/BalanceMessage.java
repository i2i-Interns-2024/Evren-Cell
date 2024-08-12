package com.i2i.evrencell.abmf;

public class BalanceMessage {

    private String msisdn;
    private Integer minute;
    private Integer data;
    private Integer sms;

    public BalanceMessage(String msisdn, Integer minute, Integer data, Integer sms) {
        this.msisdn = msisdn;
        this.minute = minute;
        this.data = data;
        this.sms = sms;
    }

    public BalanceMessage() {
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public Integer getSms() {
        return sms;
    }

    public void setSms(Integer sms) {
        this.sms = sms;
    }
}
