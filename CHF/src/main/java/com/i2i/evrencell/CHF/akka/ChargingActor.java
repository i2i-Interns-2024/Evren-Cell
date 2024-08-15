package com.i2i.evrencell.CHF.akka;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.i2i.evrencell.CHF.calculator.BalanceCalculator;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.SmsTransaction;
import org.sk.i2i.evren.VoiceTransaction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChargingActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final BalanceCalculator balanceCalculator = new BalanceCalculator(new VoltdbOperator());

    private int count = 0;

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DataTransaction.class, this::handleDataMessage).match(SmsTransaction.class, this::handleSmsMessage).match(VoiceTransaction.class, this::handleVoiceMessage).matchAny(message -> log.warning("Received unknown message: {}", message)).build();
    }

    private void handleDataMessage(DataTransaction dataMessage) {
        count++;
        processTransaction(() -> balanceCalculator.calculateDataRequest(dataMessage), "Data message received. Total count: " + count);
    }

    private void handleSmsMessage(SmsTransaction smsMessage) {
        count++;
        processTransaction(() -> balanceCalculator.calculateSmsRequest(smsMessage), "SMS message received. Total count: " + count);
    }

    private void handleVoiceMessage(VoiceTransaction voiceMessage) {
        count++;
        processTransaction(() -> balanceCalculator.calculateVoiceRequest(voiceMessage), "Voice message received. Total count: " + count);
    }

    private void processTransaction(Runnable calculationTask, String logMessage) {
        Executor executor = getContext().dispatcher();
        CompletableFuture<Void> future = CompletableFuture.runAsync(calculationTask, executor);

        future.whenComplete((result, failure) -> {
            if (failure != null) {
                log.error("Failed to process transaction");
            } else {
                log.info(logMessage);
            }
        });
    }
}
