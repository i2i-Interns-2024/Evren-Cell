package com.i2i.evrencell.kafka.deserializer;

import com.i2i.evrencell.kafka.message.BalanceMessage;

public class BalanceMessageDeserializer extends GenericMessageDeserializer<BalanceMessage> {
    public BalanceMessageDeserializer() {
        super(BalanceMessage.class);
    }
}
