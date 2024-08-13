package com.i2i.evrencell.kafka.message;


import java.sql.Time;
import java.sql.Timestamp;

public class NotificationMessage implements Message {

    private final String name;
    private final String lastname;
    private final String msisdn;
    private final String email;
    private final BalanceType type;
    private final Integer amount;
    private final String threshold;
    private final Timestamp timestamp ;


    public NotificationMessage(String name, String lastname, String msisdn, String email, BalanceType type, Integer amount, String threshold,Timestamp timestamp) {
        this.name = name;
        this.lastname = lastname;
        this.msisdn = msisdn;
        this.email = email;
        this.type = type;
        this.amount = amount;
        this.threshold = threshold;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getEmail() {
        return email;
    }

    public BalanceType getType() {
        return type;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getThreshold() {
        return threshold;
    }

    public Timestamp getTimestamp() {return timestamp;}
}
