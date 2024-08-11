package org.sk.i2i.evren.TGF.actors;

import akka.actor.AbstractActor;
import akka.actor.DeadLetter;
import org.sk.i2i.evren.DataTransaction;
import org.sk.i2i.evren.SmsTransaction;
import org.sk.i2i.evren.TGF.DeadLetterStats;
import org.sk.i2i.evren.VoiceTransaction;

public class DeadLetterListener extends AbstractActor {
    DeadLetterStats deadLetterStats;

    public DeadLetterListener(DeadLetterStats deadLetterStats) {
        this.deadLetterStats = deadLetterStats;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DeadLetter.class, deadLetter -> {

                    if(deadLetter.message() instanceof DataTransaction)
                        deadLetterStats.incrementData();
                    else if (deadLetter.message() instanceof VoiceTransaction)
                        deadLetterStats.incrementVoice();
                    else if (deadLetter.message() instanceof SmsTransaction)
                        deadLetterStats.incrementSms();

                }).build();
    }
}
