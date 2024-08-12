package com.i2i.evrencell.akka;

import akka.actor.AbstractActor;
import com.i2i.evrencell.calculator.BalanceCalculator;
import com.i2i.evrencell.messages.DataMessage;
import com.i2i.evrencell.messages.SmsMessage;
import com.i2i.evrencell.messages.VoiceMessage;

import org.sk.i2i.evren.*;


public class ChargingActor extends AbstractActor {

    private final BalanceCalculator balanceCalculator;

    public ChargingActor(BalanceCalculator balanceCalculator) {
        this.balanceCalculator = balanceCalculator;
    }

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