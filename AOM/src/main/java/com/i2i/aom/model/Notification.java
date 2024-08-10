package com.i2i.aom.model;

import java.sql.Timestamp;

public class Notification {
    private Timestamp notificationTime;
    private String notificationType;
    private Integer customerId;

    public Notification(Timestamp notificationTime, String notificationType, Integer customerId) {
        this.notificationTime = notificationTime;
        this.notificationType = notificationType;
        this.customerId = customerId;
    }

    public Notification() {}

    public Timestamp getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Timestamp notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationTime=" + notificationTime +
                ", notificationType='" + notificationType + '\'' +
                ", customerId=" + customerId +
                '}';
    }
}
