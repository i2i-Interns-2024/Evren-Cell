package com.i2i.evrencell.CHF.akka;

import akka.actor.AbstractActor;
import com.i2i.evrencell.CHF.calculator.BalanceCalculator;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.SmsTransaction;
import org.sk.i2i.evren.VoiceTransaction;


public class ChargingActor extends AbstractActor {

    private final BalanceCalculator balanceCalculator = new BalanceCalculator(new VoltdbOperator());

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
        System.out.println("Received data message");
        balanceCalculator.calculateDataRequest(dataMessage);
    }

    private void handleSmsMessage(SmsTransaction smsMessage) {
        System.out.println("Received sms message");
        balanceCalculator.calculateSmsRequest(smsMessage);
    }

    private void handleVoiceMessage(VoiceTransaction voiceMessage) {
        System.out.println("Received voice message");
        balanceCalculator.calculateVoiceRequest(voiceMessage);
    }
}