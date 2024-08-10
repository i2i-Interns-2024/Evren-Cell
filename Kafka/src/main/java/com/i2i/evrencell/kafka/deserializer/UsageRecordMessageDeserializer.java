package com.i2i.evrencell.kafka.deserializer;

import com.i2i.evrencell.kafka.message.UsageRecordMessage;

public class UsageRecordMessageDeserializer extends GenericMessageDeserializer<UsageRecordMessage> {
    public UsageRecordMessageDeserializer() {
        super(UsageRecordMessage.class);
    }
}
