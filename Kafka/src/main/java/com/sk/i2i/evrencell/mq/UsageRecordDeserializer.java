package com.sk.i2i.evrencell.mq;

public class UsageRecordDeserializer extends GenericMessageDeserializer<UsageRecord> {
    public UsageRecordDeserializer() {
        super(UsageRecord.class);
    }
}
