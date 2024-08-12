package com.i2i.evrencell.kafka.message;

public class NotificationMessage implements Message {

    private final String name;
    private final String lastname;
    private final String msisdn;
    private final String email;
    private final BalanceType type;
    private final Integer amount;
    private final String threshold;
    private final String notificationTime;

    public NotificationMessage(String name, String lastname, String msisdn, String email, BalanceType type, Integer amount, String threshold, String notificationTime) {
        this.name = name;
        this.lastname = lastname;
        this.msisdn = msisdn;
        this.email = email;
        this.type = type;
        this.amount = amount;
        this.threshold = threshold;
        this.notificationTime = notificationTime;
    }

    public NotificationMessage() {
       //initilize variables
        this.name = "";
        this.lastname = "";
        this.msisdn = "";
        this.email = "";
        this.type = BalanceType.SMS;
        this.amount = 0;
        this.threshold = "";
        this.notificationTime="";
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

    public String getNotificationTime() {
        return notificationTime;
    }
}
