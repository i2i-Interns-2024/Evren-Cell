package com.ikbalci.messages;

import java.io.Serializable;
import java.util.Date;

public class SmsMessage extends Message implements Serializable {
    private int location;
    private String msisdn;
    private String bMsisdn;
    private Date date;

    public SmsMessage(int location, String msisdn, String bMsisdn) {
        super(location, msisdn);
        this.location = location;
        this.msisdn = msisdn;
        this.bMsisdn = bMsisdn;
        this.date = new Date();
    }

    public int getLocation() {
        return location;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getbMsisdn() {
        return bMsisdn;
    }

    public Date getDate() {
        return date;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setbMsisdn(String bMsisdn) {
        this.bMsisdn = bMsisdn;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

