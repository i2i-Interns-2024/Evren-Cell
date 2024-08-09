package com.sk.i2i.evrencell.mq;

import java.util.Date;

public class Notification extends Message {
    public Notification(String msisdn) {
        super(msisdn);
    }
    String notificationType;
    Date notificationDate;
    String notificationText;

    public String getNotificationType() {
        return notificationType;
    }
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
    public Date getNotificationDate() {
        return notificationDate;
    }
    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }
    public String getNotificationText() {
        return notificationText;
    }
    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

}
