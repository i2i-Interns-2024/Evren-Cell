package com.i2i.evrencell.messages;

public class DataMessage {

    private final String msisdn;
    private final int dataUsage;

    public DataMessage(String msisdn, int dataUsage) {
        this.msisdn = msisdn;
        this.dataUsage = dataUsage;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public int getDataUsage() {
        return dataUsage;
    }
}
