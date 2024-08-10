package com.sk.i2i.evrencell.mq;

public class NotificationDeserializer extends GenericMessageDeserializer<Notification>
{
    public NotificationDeserializer() {
        super(Notification.class);
    }

}
