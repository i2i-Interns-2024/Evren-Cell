package org.sk.i2i.evren.TGF.actors;

import akka.actor.AbstractActor;
import akka.actor.DeadLetter;
import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.SmsTransaction;
import org.sk.i2i.evren.TGF.management.StatsManager;
import org.sk.i2i.evren.TGF.trafficGenerators.TrafficGenerator;
import org.sk.i2i.evren.VoiceTransaction;

public class DeadLetterListener extends AbstractActor {
    StatsManager statsManager;

    public DeadLetterListener(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DeadLetter.class, deadLetter -> {

                    if(deadLetter.message() instanceof DataTransaction)
                        statsManager.incrementDeadCounter(TrafficGenerator.TransTypes.DATA);
                    else if (deadLetter.message() instanceof VoiceTransaction)
                        statsManager.incrementDeadCounter(TrafficGenerator.TransTypes.VOICE);
                    else if (deadLetter.message() instanceof SmsTransaction)
                        statsManager.incrementDeadCounter(TrafficGenerator.TransTypes.SMS);

                }).build();
    }
}
