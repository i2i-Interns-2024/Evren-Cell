package com.sk.i2i.smsapp.model;

public class UserInfo {
    private String msisdn;
    private int balanceData;
    private int balanceSms;
    private int balanceMinutes;
    private String sdate;
    private String edate;

    // Getters and Setters
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getBalanceData() {
        return balanceData;
    }

    public void setBalanceData(int balanceData) {
        this.balanceData = balanceData;
    }

    public int getBalanceSms() {
        return balanceSms;
    }

    public void setBalanceSms(int balanceSms) {
        this.balanceSms = balanceSms;
    }

    public int getBalanceMinutes() {
        return balanceMinutes;
    }

    public void setBalanceMinutes(int balanceMinutes) {
        this.balanceMinutes = balanceMinutes;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }
}
