package com.ikbalci.messages;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private int location;
    private String msisdn;
    private Date date;

    public Message(int location, String msisdn) {
        this.location = location;
        this.msisdn = msisdn;
        this.date = new Date();
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}