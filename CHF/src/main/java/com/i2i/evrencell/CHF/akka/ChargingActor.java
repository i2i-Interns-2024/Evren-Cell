package com.i2i.evrencell.CHF.akka;

import akka.actor.AbstractActor;
import com.i2i.evrencell.CHF.calculator.BalanceCalculator;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.SmsTransaction;
import org.sk.i2i.evren.VoiceTransaction;


public class ChargingActor extends AbstractActor {

    private final BalanceCalculator balanceCalculator = new BalanceCalculator(new VoltdbOperator());
    //TODO asynchronous voltdb connection

    public int count = 0;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DataTransaction.class, this::handleDataMessage)
                .match(SmsTransaction.class, this::handleSmsMessage)
                .match(VoiceTransaction.class, this::handleVoiceMessage)
                .matchAny(message -> System.out.println("Received unknown message: " + message))
                .build();
    }

    private void handleDataMessage(DataTransaction dataMessage) {
        balanceCalculator.calculateDataRequest(dataMessage);
        count++;
        System.out.println("Data message received");
        System.out.println(count);
    }

    private void handleSmsMessage(SmsTransaction smsMessage) {
        balanceCalculator.calculateSmsRequest(smsMessage);
        count++;
        System.out.println("SMS message received");
        System.out.println(count);
    }

    private void handleVoiceMessage(VoiceTransaction voiceMessage) {
        balanceCalculator.calculateVoiceRequest(voiceMessage);
        count++;
        System.out.println("Voice message received");
        System.out.println(count);
    }
}