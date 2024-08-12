package com.i2i.evrencell.kafka.deserializer;

import com.i2i.evrencell.kafka.message.NotificationMessage;

public class NotificationMessageDeserializer extends GenericMessageDeserializer<NotificationMessage>
{
    public NotificationMessageDeserializer() {
        super(NotificationMessage.class);
    }

}
