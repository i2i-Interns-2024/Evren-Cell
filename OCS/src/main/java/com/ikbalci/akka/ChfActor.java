package com.ikbalci.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.ikbalci.calculator.BalanceCalculator;
import com.ikbalci.messages.DataMessage;
import com.ikbalci.messages.SmsMessage;
import com.ikbalci.messages.VoiceMessage;

public class ChfActor extends AbstractActor {

    private final BalanceCalculator balanceCalculator = new BalanceCalculator();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DataMessage.class, balanceCalculator::calculateDataRequest)
                .match(SmsMessage.class, balanceCalculator::calculateSmsRequest)
                .match(VoiceMessage.class, balanceCalculator::calculateVoiceRequest)
                .build();
    }

    public static Props props() {
        return Props.create(ChfActor.class);
    }
}
