package com.ikbalci.messages;

import java.io.Serializable;
import java.util.Date;

public class VoiceMessage extends Message implements Serializable {
    private int location;
    private String msisdn;
    private int duration;
    private String bMsisdn;
    private Date date;

    public VoiceMessage(int location, String msisdn, int duration, String bMsisdn) {
        super(location, msisdn);
        this.location = location;
        this.msisdn = msisdn;
        this.duration = duration;
        this.bMsisdn = bMsisdn;
        this.date = new Date();
    }

    public int getLocation() {
        return location;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public int getDuration() {
        return duration;
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

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setbMsisdn(String bMsisdn) {
        this.bMsisdn = bMsisdn;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
