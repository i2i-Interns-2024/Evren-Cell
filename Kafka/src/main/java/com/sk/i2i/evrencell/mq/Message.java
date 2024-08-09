package com.sk.i2i.evrencell.mq;

public class Message {

    private String msisdn;


    public Message(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
}
