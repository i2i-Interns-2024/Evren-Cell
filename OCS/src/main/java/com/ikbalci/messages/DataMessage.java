package com.ikbalci.messages;

import java.io.Serializable;
import java.util.Date;

public class DataMessage extends Message implements Serializable {
    private int location;
    private String msisdn;
    private int dataUsage;
    private int rGroup;
    private Date date;

    public DataMessage(int location, String msisdn, int dataUsage, int rGroup) {
        super(location, msisdn);
        this.location = location;
        this.msisdn = msisdn;
        this.dataUsage = dataUsage;
        this.rGroup = rGroup;
        this.date = new Date();
    }

    public int getLocation() {
        return location;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public int getDataUsage() {
        return dataUsage;
    }

    public int getRGroup() {
        return rGroup;
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

    public void setDataUsage(int dataUsage) {
        this.dataUsage = dataUsage;
    }

    public void setrGroup(int rGroup) {
        this.rGroup = rGroup;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}