package com.sk.i2i.evrencell.mq;

import java.io.Serializable;

public class Message implements Serializable {

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
