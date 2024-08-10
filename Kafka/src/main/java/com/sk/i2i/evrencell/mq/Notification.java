package com.sk.i2i.evrencell.mq;

import java.io.Serializable;
import java.util.Date;

public class Notification extends Message implements Serializable {
    public Notification(String msisdn) {
        super(msisdn);
    }
    String notificationType;
    String name;
    String surname;
    String notificationText;

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
