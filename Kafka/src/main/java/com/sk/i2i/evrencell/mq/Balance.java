package com.sk.i2i.evrencell.mq;

import java.io.Serializable;

public class Balance extends Message implements Serializable {

    private Integer minute; //400
    private Integer data;
    private Integer sms;

    public Balance(String msisdn, Integer minute, Integer data, Integer sms) {
        super(msisdn);
        this.minute = minute;
        this.data = data;
        this.sms = sms;
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
