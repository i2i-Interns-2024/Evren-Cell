package com.sk.i2i.evrencell.mq;

public class BalanceMessageDeserializer extends GenericMessageDeserializer<Balance> {
    public BalanceMessageDeserializer() {
        super(Balance.class);
    }
}
