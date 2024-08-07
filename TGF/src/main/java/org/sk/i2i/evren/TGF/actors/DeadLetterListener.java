package org.sk.i2i.evren.TGF.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.DeadLetter;
import org.sk.i2i.evren.TGF.DTO.DataTransaction;

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
                            System.out.println("dead letter counter:  " + deadLetterCounter);
                        }
                    }
                    else {
                        System.out.println("Received dead letter: " + deadLetter.message() +
                                " from " + deadLetter.sender() +
                                " to " + deadLetter.recipient());
                    }


                })
                .build();
    }
}
