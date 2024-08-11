package com.i2i.evrencell.akka;

import akka.actor.AbstractActor;
import com.i2i.evrencell.calculator.BalanceCalculator;
import com.i2i.evrencell.messages.DataMessage;
import com.i2i.evrencell.messages.SmsMessage;
import com.i2i.evrencell.messages.VoiceMessage;

public class ChargingActor extends AbstractActor {

    private final BalanceCalculator balanceCalculator;

    public ChargingActor(BalanceCalculator balanceCalculator) {
        this.balanceCalculator = balanceCalculator;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DataMessage.class, this::handleDataMessage)
                .match(SmsMessage.class, this::handleSmsMessage)
                .match(VoiceMessage.class, this::handleVoiceMessage)
                .matchAny(message -> System.out.println("Received unknown message: " + message))
                .build();
    }

    private void handleDataMessage(DataMessage dataMessage) {
        balanceCalculator.calculateDataRequest(dataMessage);
    }

    private void handleSmsMessage(SmsMessage smsMessage) {
        balanceCalculator.calculateSmsRequest(smsMessage);
    }

    private void handleVoiceMessage(VoiceMessage voiceMessage) {
        balanceCalculator.calculateVoiceRequest(voiceMessage);
    }
}