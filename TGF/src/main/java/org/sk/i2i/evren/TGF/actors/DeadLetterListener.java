package org.sk.i2i.evren.TGF.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.DeadLetter;
import org.sk.i2i.evren.TGF.DTO.DataTransaction;
import org.sk.i2i.evren.TGF.DTO.SmsTransaction;
import org.sk.i2i.evren.TGF.DTO.VoiceTransaction;

public class DeadLetterListener extends AbstractActor {

    int deadLetterCounter = 0;

    public static Props props() {
        return Props.create(DeadLetterListener.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DeadLetter.class, deadLetter -> {

                    if(deadLetter.message() instanceof DataTransaction) {

                        deadLetterCounter++;
                        if(deadLetterCounter % 1000 == 0) {
                            System.out.print("deadLetter counter:  " + deadLetterCounter + "\r");
                        }

                    } else if (deadLetter.message() instanceof VoiceTransaction) {

                        deadLetterCounter++;
                        if(deadLetterCounter % 1000 == 0) {
                            System.out.print("deadLetter counter:  " + deadLetterCounter + "\r");
                        }

                    } else if (deadLetter.message() instanceof SmsTransaction) {

                        deadLetterCounter++;
                        if(deadLetterCounter % 1000 == 0) {
                            System.out.print("deadLetter counter:  " + deadLetterCounter + "\r");
                        }

                    } /*else {
                        System.out.println("Received dead letter: " + deadLetter.message() +
                                " from " + deadLetter.sender() +
                                " to " + deadLetter.recipient());
                    }*/


                })
                .build();
    }
}
