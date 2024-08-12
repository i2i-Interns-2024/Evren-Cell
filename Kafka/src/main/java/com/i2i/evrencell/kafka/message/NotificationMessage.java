package com.i2i.evrencell.kafka.message;

public class NotificationMessage implements Message {
    private String msisdn;
    private String notificationType;
    private String name;
    private String surname;
    private String notificationText;

    public NotificationMessage(String msisdn, String notificationType, String name, String surname, String notificationText) {
        this.msisdn = msisdn;
        this.notificationType = notificationType;
        this.name = name;
        this.surname = surname;
        this.notificationText = notificationText;
    }

    public NotificationMessage() {
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }
}
