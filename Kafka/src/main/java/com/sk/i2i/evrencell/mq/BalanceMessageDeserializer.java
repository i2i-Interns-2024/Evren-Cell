package com.sk.i2i.evrencell.mq;

public class BalanceMessageDeserializer extends GenericMessageDeserializer<BalanceMessage> {
    public BalanceMessageDeserializer() {
        super(BalanceMessage.class);
    }
}
